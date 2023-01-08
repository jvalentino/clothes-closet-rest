package com.github.jvalentino.clothescloset.entity

import groovy.transform.CompileDynamic

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotBlank

/**
 * Represents a school
 * @author john.valentino
 */
@CompileDynamic
@Entity
@Table(name = 'school')
class School {

    @Id
    @NotBlank(message = 'label cannot be blank')
    String label

}
