package com.github.jvalentino.clothescloset.dto

import groovy.transform.CompileDynamic

/**
 * Return result for making an appointment
 * @author john.valentino
 */
@CompileDynamic
class MakeAppointmentResultDto extends ResultDto {

    Long appointmentId
    List<Long> visitIds = []
    List<String> studentIds = []

}
