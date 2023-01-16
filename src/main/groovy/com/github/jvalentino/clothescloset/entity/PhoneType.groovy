package com.github.jvalentino.clothescloset.entity

import groovy.transform.CompileDynamic

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotBlank

/**
 * Represents the phone type, like mobile
 * @author john.valentino
 */
@CompileDynamic
@Entity
@Table(name = 'phone_type')
class PhoneType {

    @Id
    @NotBlank(message = 'label cannot be blank')
    String label

    @Column(name = 'order_position')
    Integer orderPosition

}
