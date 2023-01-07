package com.github.jvalentino.clothescloset.entity

import groovy.transform.CompileDynamic

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.validation.constraints.NotBlank
import java.sql.Timestamp

@CompileDynamic
@Entity
@Table(name = "appointment")
class Appointment {

    @Id @GeneratedValue
    Long id

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "guardian_email", referencedColumnName = "email")
    Guardian guardian

    Timestamp datetime

    Integer year

    String semester
}
