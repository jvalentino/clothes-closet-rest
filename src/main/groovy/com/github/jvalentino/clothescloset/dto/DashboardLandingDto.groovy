package com.github.jvalentino.clothescloset.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.jvalentino.clothescloset.entity.Appointment
import groovy.transform.CompileDynamic

/**
 * Represents all the content on the initial dashboard
 * @author john.valentino
 */
@CompileDynamic
class DashboardLandingDto {

    String timeZone
    String currentDateString
    Integer year
    String semester
    Long onWaitList = 0L
    @JsonIgnore
    Date currentDate
    String semesterStartDateString
    @JsonIgnore
    Date semesterStartDate
    String semesterEndDateString
    @JsonIgnore
    Date semesterEndDate
    ReportingDto report
    List<Appointment> upcomingAppointments = []
    List<Appointment> requireAttention = []

}
