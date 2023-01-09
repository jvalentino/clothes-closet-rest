package com.github.jvalentino.clothescloset.dto

import com.github.jvalentino.clothescloset.entity.Gender
import com.github.jvalentino.clothescloset.entity.Grade
import com.github.jvalentino.clothescloset.entity.PhoneType
import com.github.jvalentino.clothescloset.entity.School
import groovy.transform.CompileDynamic

/**
 * General settings to be able to render appointment options
 * @author john.valentino
 */
@CompileDynamic
class AppointmentSettingsDto {

    String startDateIso
    String endDateIso
    String timeZone
    List<Gender> genders = []
    List<School> schools = []
    List<Grade> grades = []
    List<PhoneType> phoneTypes = []
    List<EventDto> events = []

}
