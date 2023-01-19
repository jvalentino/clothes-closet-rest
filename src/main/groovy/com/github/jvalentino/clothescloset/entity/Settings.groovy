package com.github.jvalentino.clothescloset.entity

import groovy.transform.CompileDynamic

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * Represents general settings which are gender specific, such as
 * number of tops boys can have during a visit.
 * @author john.valentino
 */
@CompileDynamic
@Entity
@Table(name = 'settings')
class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = 'settings_id')
    Long settingsId
    String gender
    Integer quantity
    String label

}
