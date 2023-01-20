package com.github.jvalentino.clothescloset.dto

import com.github.jvalentino.clothescloset.entity.Guardian
import com.github.jvalentino.clothescloset.entity.Student
import groovy.transform.CompileDynamic

import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * Payload body for creating a new appointment
 * @author john.valentino
 */
@CompileDynamic
class MakeAppointmentDto {

    @NotBlank(message = 'datetime cannot be blank')
    String datetime

    String timeZone = 'America/Chicago'

    String locale = 'en'

    @NotNull(message = 'guardian cannot be blank')
    @Valid
    Guardian guardian

    @NotEmpty(message = 'students cannot be empty')
    @Valid
    List<Student> students

}
