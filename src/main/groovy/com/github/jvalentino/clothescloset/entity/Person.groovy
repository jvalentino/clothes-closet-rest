package com.github.jvalentino.clothescloset.entity

import groovy.transform.CompileDynamic

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotBlank

@CompileDynamic
@Entity
@Table(name = "person")
class Person {

    @Id @GeneratedValue
    Long id

    String relation

}
