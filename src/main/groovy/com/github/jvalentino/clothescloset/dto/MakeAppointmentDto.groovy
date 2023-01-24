package com.github.jvalentino.clothescloset.dto

import com.github.jvalentino.clothescloset.entity.Guardian
import com.github.jvalentino.clothescloset.entity.Student
import groovy.transform.CompileDynamic

import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * Payload body for creating a new appointment
 * @author john.valentino
 */
@CompileDynamic
@SuppressWarnings(['NoJavaUtilDate'])
class MakeAppointmentDto {

    String datetime

    String timeZone = 'America/Chicago'

    String locale = 'en'

    boolean waitlist = false

    Date currentDate = new Date()

    @NotNull(message = 'guardian cannot be blank')
    @Valid
    Guardian guardian

    @NotEmpty(message = 'students cannot be empty')
    @Valid
    List<Student> students = []

}
