package com.github.jvalentino.clothescloset.entity

import groovy.transform.CompileDynamic

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotBlank

/**
 * Represents the authorized user
 * @author john.valentino
 */
@CompileDynamic
@Entity
@Table(name = 'auth_user')
class AuthUser {

    @Id
    @NotBlank(message = 'email cannot be blank')
    String email

}
