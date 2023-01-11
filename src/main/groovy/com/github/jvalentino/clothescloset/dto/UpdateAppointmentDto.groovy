package com.github.jvalentino.clothescloset.dto

import com.github.jvalentino.clothescloset.entity.Visit
import groovy.transform.CompileDynamic

/**
 * Used for updating the visit numbers in an appointment
 * @author john.valentino
 */
@CompileDynamic
class UpdateAppointmentDto {

    Long appointmentId
    List<Visit> visits = []

}
