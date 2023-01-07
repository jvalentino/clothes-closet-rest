package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.dto.MakeAppointmentDto
import com.github.jvalentino.clothescloset.dto.ResultDto
import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Student
import com.github.jvalentino.clothescloset.entity.Visit
import com.github.jvalentino.clothescloset.repo.AppointmentRepository
import com.github.jvalentino.clothescloset.repo.GuardianRepository
import com.github.jvalentino.clothescloset.repo.StudentRepository
import com.github.jvalentino.clothescloset.repo.VisitRepository
import com.github.jvalentino.clothescloset.util.DateUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import java.sql.Timestamp

@RestController
@Validated
class AppointmentController {

    @Autowired
    GuardianRepository guardianRepository

    @Autowired
    StudentRepository studentRepository

    @Autowired
    AppointmentRepository appointmentRepository

    @Autowired
    VisitRepository visitRepository

    @PostMapping("/appointment/schedule")
    ResultDto schedule(@Valid @RequestBody MakeAppointmentDto appointment) {
        // handle the guardian
        guardianRepository.save(appointment.guardian)

        // handle each student
        for (Student student : appointment.students) {
            studentRepository.save(student)
        }

        // create a new appointment
        Appointment app = new Appointment()
        app.guardian = appointment.guardian
        app.datetime = new Timestamp(DateUtil.toDate(appointment.datetime).time)
        app.year = app.datetime.year

        if (app.datetime.month >= 0 && app.datetime.month <= 5) {
            app.semester = 'Spring'
        } else {
            app.semester = 'Fall'
        }

        app = appointmentRepository.save(app)

        // create a visit for each student
        for (Student student : appointment.students) {
            Visit visit = new Visit()
            visit.appointment = app
            visit.student = student
            visit.happened = false
            visitRepository.save(visit)
        }

        return new ResultDto()
    }

    @ExceptionHandler(ConstraintViolationException)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResultDto handleConstraintViolationException(ConstraintViolationException e) {
        ResultDto result = new ResultDto(success:false)

        for (ConstraintViolation v : e.getConstraintViolations()) {
            result.messages.add(v.getMessage())
        }

        result
    }

}
