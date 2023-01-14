package com.github.jvalentino.clothescloset.dto

import groovy.transform.CompileDynamic

/**
 * Used to represent calendar availability slots, and how they were calculated
 * because debugging
 * @author john.valentino
 */
@CompileDynamic
class AvailabilityDto {

    List<TimeRangeDto> ranges = []
    List<TimeRangeDto> adjustedRanges = []
    List<String> startDateTimes = []
    List<String> availabilities = []

}
