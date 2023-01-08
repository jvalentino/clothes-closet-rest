package com.github.jvalentino.clothescloset.dto

import com.github.jvalentino.clothescloset.entity.Gender
import com.github.jvalentino.clothescloset.entity.Grade
import com.github.jvalentino.clothescloset.entity.School
import groovy.transform.CompileDynamic

/**
 * General settings to be able to render appointment options
 * @author john.valentino
 */
@CompileDynamic
class AppointmentSettingsDto {

    List<Gender> genders = []
    List<School> schools = []
    List<Grade> grades = []

}
