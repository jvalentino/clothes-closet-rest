package com.github.jvalentino.clothescloset.entity

import groovy.transform.CompileDynamic

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotBlank

/**
 * Represents the semester
 * @author john.valentino
 */
@CompileDynamic
@Entity
@Table(name = 'semester')
class Semester {

    @Id
    @NotBlank(message = 'label cannot be blank')
    String label

}
