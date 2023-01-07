package com.github.jvalentino.clothescloset.entity

import groovy.transform.CompileDynamic

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
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

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = 'appointment_id', referencedColumnName = 'id')
    Appointment appointment

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = 'student_id', referencedColumnName = 'id')
    Student student

    @OneToOne(cascade = CascadeType.MERGE)
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
