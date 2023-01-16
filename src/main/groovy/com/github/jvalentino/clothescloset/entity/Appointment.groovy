package com.github.jvalentino.clothescloset.entity

import groovy.transform.CompileDynamic

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
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
    @Column(name = 'appointment_id')
    Long appointmentId

    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = 'guardian_email', referencedColumnName = 'email')
    Guardian guardian

    Timestamp datetime

    Integer year

    String semester

    @Transient
    String datetimeIso

    Boolean happened

    @Column(name = 'event_id')
    String eventId

    @OneToMany(mappedBy='appointment', fetch = FetchType.LAZY)
    Set<Visit> visits

}
