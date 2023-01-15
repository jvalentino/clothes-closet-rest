package com.github.jvalentino.clothescloset.dto

import com.github.jvalentino.clothescloset.entity.Visit
import groovy.transform.CompileDynamic

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * Used for updating the visit numbers in an appointment
 * @author john.valentino
 */
@CompileDynamic
class UpdateAppointmentDto {

    @NotNull(message = 'appointmentId cannot be blank')
    Long appointmentId

    @Valid
    List<Visit> visits = []

}
