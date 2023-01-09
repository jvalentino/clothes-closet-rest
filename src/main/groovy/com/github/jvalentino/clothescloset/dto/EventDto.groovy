package com.github.jvalentino.clothescloset.dto

import groovy.transform.CompileDynamic

/**
 * Used to represent a booked range on the calendar
 * @author john.valentino
 */
@CompileDynamic
class EventDto {

    String title
    String start
    String end
    String color

}
