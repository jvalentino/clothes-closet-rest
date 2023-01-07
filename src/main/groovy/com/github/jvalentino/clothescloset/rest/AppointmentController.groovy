package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.dto.MakeAppointmentDto
import com.github.jvalentino.clothescloset.dto.ResultDto
import com.github.jvalentino.clothescloset.repo.GuardianRepository
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

@RestController
@Validated
class AppointmentController {

    @Autowired
    GuardianRepository guardianRepository

    @PostMapping("/appointment/schedule")
    ResultDto newEmployee(@Valid @RequestBody MakeAppointmentDto appointment) {
        guardianRepository.save(appointment.guardian)
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
