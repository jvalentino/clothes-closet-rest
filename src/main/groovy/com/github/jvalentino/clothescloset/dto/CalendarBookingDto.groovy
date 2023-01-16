package com.github.jvalentino.clothescloset.dto

import com.google.api.services.calendar.model.Event
import groovy.transform.CompileDynamic

/**
 * Representation of all the information needed for appointments
 * @author john.valentino
 */
@CompileDynamic
class CalendarBookingDto {

    AvailabilityDto availability
    List<Event> calendarEvents = []
    List<EventDto> events = []

}
