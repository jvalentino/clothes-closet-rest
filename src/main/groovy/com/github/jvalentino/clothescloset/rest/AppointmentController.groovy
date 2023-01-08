package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.dto.AppointmentSettingsDto
import com.github.jvalentino.clothescloset.dto.MakeAppointmentDto
import com.github.jvalentino.clothescloset.dto.ResultDto
import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Student
import com.github.jvalentino.clothescloset.entity.Visit
import com.github.jvalentino.clothescloset.repo.AppointmentRepository
import com.github.jvalentino.clothescloset.repo.GuardianRepository
import com.github.jvalentino.clothescloset.repo.SettingsRepository
import com.github.jvalentino.clothescloset.repo.StudentRepository
import com.github.jvalentino.clothescloset.repo.VisitRepository
import com.github.jvalentino.clothescloset.util.DateUtil
import groovy.transform.CompileDynamic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import java.sql.Timestamp

/**
 * REST endpoint for dealing wth appointments
 * @author john.valentino
 */
@CompileDynamic
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

    @Autowired
    SettingsRepository settingsRepository

    @PostMapping('/appointment/schedule')
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
        app.year = DateUtil.getYear(app.datetime)

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

        new ResultDto()
    }

    @GetMapping('/appointment/settings')
    AppointmentSettingsDto getSettings() {
        AppointmentSettingsDto result = new AppointmentSettingsDto()
        result.settings = settingsRepository.retrieveAll()
        result
    }

    @ExceptionHandler(ConstraintViolationException)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResultDto handleConstraintViolationException(ConstraintViolationException e) {
        ResultDto result = new ResultDto(success:false)

        for (ConstraintViolation v : e.constraintViolations) {
            result.messages.add(v.message)
        }

        result
    }

}
