package com.github.jvalentino.clothescloset.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileDynamic

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * Represents a visit of an individual student or person that was the result
 * of an appointment.
 * @author john.valentino
 */
@CompileDynamic
@Entity
@Table(name = 'visit')
class Visit {

    @Id @GeneratedValue
    Long id

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = 'appointment_id', referencedColumnName = 'id')
    @JsonIgnore
    Appointment appointment

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = 'student_id', referencedColumnName = 'id')
    Student student

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = 'person_id', referencedColumnName = 'id')
    Person person

    Integer socks

    Integer underwear

    Integer shoes

    Boolean happened

    Integer coats

    Integer backpacks

    Integer misc

}
