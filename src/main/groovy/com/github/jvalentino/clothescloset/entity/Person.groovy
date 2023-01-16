package com.github.jvalentino.clothescloset.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileDynamic

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

/**
 * Represents a person that came along for the visit but that is not a student
 * @author john.valentino
 */
@CompileDynamic
@Entity
@Table(name = 'person')
class Person {

    @Id @GeneratedValue
    @Column( name = 'person_id')
    Long personId

    String relation

    @OneToMany(mappedBy='person', fetch = FetchType.LAZY)
    @JsonIgnore
    Set<Visit> visits

}
