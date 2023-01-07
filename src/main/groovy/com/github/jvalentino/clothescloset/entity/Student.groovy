package com.github.jvalentino.clothescloset.entity

import groovy.transform.CompileDynamic

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotBlank

@CompileDynamic
@Entity
@Table(name = "student")
class Student {

    @Id
    @NotBlank(message = "id cannot be blank")
    String id

    @NotBlank(message = "school cannot be blank")
    String school

    @NotBlank(message = "gender cannot be blank")
    String gender

    @NotBlank(message = "grade cannot be blank")
    String grade

}
