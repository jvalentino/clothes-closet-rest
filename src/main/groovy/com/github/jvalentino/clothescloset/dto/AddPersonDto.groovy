package com.github.jvalentino.clothescloset.dto

import com.github.jvalentino.clothescloset.entity.Person
import groovy.transform.CompileDynamic

/**
 * Input for adding a person to a visit
 * @author john.valentino
 */
@CompileDynamic
class AddPersonDto {

    Long appointmentId
    Person person

}
