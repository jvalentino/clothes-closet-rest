package com.github.jvalentino.clothescloset.dto

import com.github.jvalentino.clothescloset.entity.Settings
import groovy.transform.CompileDynamic

/**
 * General settings to be able to render appointment options
 * @author john.valentino
 */
@CompileDynamic
class AppointmentSettingsDto {

    List<Settings> settings = []

}
