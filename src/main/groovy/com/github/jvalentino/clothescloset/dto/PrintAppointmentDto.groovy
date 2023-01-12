package com.github.jvalentino.clothescloset.dto

import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Settings
import groovy.transform.CompileDynamic

/**
 * Represents the appointment details needed when printing the sheet
 * @author john.valentino
 */
@CompileDynamic
class PrintAppointmentDto {

    Boolean firstTime
    String lastAppointmentDateIso
    Appointment appointment
    List<Settings> girlSettings = []
    List<Settings> boySettings = []
    List<Appointment> previous = []

}
