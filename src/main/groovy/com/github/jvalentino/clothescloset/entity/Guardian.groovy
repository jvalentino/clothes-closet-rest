package com.github.jvalentino.clothescloset.entity

import groovy.transform.CompileDynamic

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotBlank

/**
 * Represents a person responsible for one or more students
 * @author john.valentino
 */
@CompileDynamic
@Entity
@Table(name = 'guardian')
class Guardian {

    @Id
    @NotBlank(message = 'email cannot be blank')
    String email

    @Column(name = 'first_name')
    @NotBlank(message = 'firstName cannot be blank')
    String firstName

    @Column(name = 'last_name')
    @NotBlank(message = 'lastName cannot be blank')
    String lastName

    @Column(name = 'phone_number')
    @NotBlank(message = 'phoneNumber cannot be blank')
    String phoneNumber

    @Column(name = 'phone_type_label')
    @NotBlank(message = 'phoneTypeLabel cannot be blank')
    String phoneTypeLabel

}
