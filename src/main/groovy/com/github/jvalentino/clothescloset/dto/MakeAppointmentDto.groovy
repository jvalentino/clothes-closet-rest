package com.github.jvalentino.clothescloset.dto

import com.github.jvalentino.clothescloset.entity.Guardian
import com.github.jvalentino.clothescloset.entity.Student
import groovy.transform.CompileDynamic

/**
 * Payload body for creating a new appointment
 * @author john.valentino
 */
@CompileDynamic
class MakeAppointmentDto {

    String datetime

    String timeZone = 'America/Chicago'

    Guardian guardian

    List<Student> students

}
