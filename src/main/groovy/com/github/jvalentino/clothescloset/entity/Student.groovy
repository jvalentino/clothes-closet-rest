package com.github.jvalentino.clothescloset.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileDynamic

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.NotBlank

/**
 * Represents a student
 * @author john.valentino
 */
@CompileDynamic
@Entity
@Table(name = 'student')
class Student {

    @Id
    @NotBlank(message = 'studentId cannot be blank')
    @Column(name = 'student_id')
    String studentId

    @NotBlank(message = 'school cannot be blank')
    String school

    @NotBlank(message = 'gender cannot be blank')
    String gender

    @NotBlank(message = 'grade cannot be blank')
    String grade

    @OneToMany(mappedBy='student', fetch = FetchType.LAZY)
    @JsonIgnore
    Set<Visit> visits

}
