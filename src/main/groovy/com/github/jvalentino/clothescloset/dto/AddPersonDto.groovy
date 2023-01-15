package com.github.jvalentino.clothescloset.dto

import com.github.jvalentino.clothescloset.entity.Person
import groovy.transform.CompileDynamic

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * Input for adding a person to a visit
 * @author john.valentino
 */
@CompileDynamic
class AddPersonDto {

    @NotNull(message = 'appointmentId cannot be blank')
    Long appointmentId

    @Valid
    Person person

}
