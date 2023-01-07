package com.github.jvalentino.clothescloset.entity

import groovy.transform.CompileDynamic

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotBlank

/**
 * Represents gender
 * @author john.valentino
 */
@CompileDynamic
@Entity
@Table(name = 'gender')
class Gender {

    @Id
    @NotBlank(message = 'label cannot be blank')
    String label

}
