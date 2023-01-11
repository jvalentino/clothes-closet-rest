package com.github.jvalentino.clothescloset.entity

import groovy.transform.CompileDynamic

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.Transient
import java.sql.Timestamp

/**
 * Represents a schedule appointment
 * @author john.valentino
 */
@CompileDynamic
@Entity
@Table(name = 'appointment')
class Appointment {

    @Id @GeneratedValue
    Long id

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = 'guardian_email', referencedColumnName = 'email')
    Guardian guardian

    Timestamp datetime

    Integer year

    String semester

    @Transient
    String datetimeIso

}
