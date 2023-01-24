package com.github.jvalentino.clothescloset.dto

import groovy.transform.CompileDynamic

/**
 * DTO used for moving an appointment from the wait list into a data/time slot
 * @author john.valentino
 */
@CompileDynamic
class MoveFromWaitListDto {

    String datetime
    String timeZone = 'America/Chicago'
    Long appointmentId

}
