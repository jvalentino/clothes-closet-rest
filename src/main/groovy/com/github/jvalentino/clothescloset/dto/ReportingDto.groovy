package com.github.jvalentino.clothescloset.dto

import groovy.transform.CompileDynamic

/**
 * Used to contain reporting information
 * @author john.valentino
 */
@CompileDynamic
class ReportingDto {

    String start
    String end

    long socks = 0
    long underwear = 0
    long shoes = 0
    long coats = 0
    long backpacks = 0
    long misc = 0
    long total = 0

    long students = 0
    long persons = 0
    long totalPeople = 0

    long appointents = 0

}
