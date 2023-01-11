package com.github.jvalentino.clothescloset.dto

import com.github.jvalentino.clothescloset.entity.Appointment
import groovy.transform.CompileDynamic

/**
 * Represents the result of doing a search on appointments
 * @author john.valentino
 */
@CompileDynamic
class AppointmentSearchDto {

    String date
    String name
    List<Appointment> appointments = []

}
