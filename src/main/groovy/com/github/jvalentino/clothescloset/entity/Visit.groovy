package com.github.jvalentino.clothescloset.entity

import groovy.transform.CompileDynamic

import javax.persistence.*
import java.sql.Timestamp

@CompileDynamic
@Entity
@Table(name = "visit")
class Visit {

    @Id @GeneratedValue
    Long id

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "appointment_id", referencedColumnName = "id")
    Appointment appointment

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    Student student

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    Person person

    Integer socks

    Integer underwear

    Integer shoes

    Boolean happened

    Integer coats

    Integer backpacks

    Integer misc
}
