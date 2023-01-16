package com.github.jvalentino.clothescloset.entity

import com.opencsv.bean.CsvBindByPosition
import groovy.transform.CompileDynamic

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Represents an accepted Student ID
 * @author john.valentino
 */
@CompileDynamic
@Entity
@Table(name = 'accepted_id')
class AcceptedId {

    @Id
    @CsvBindByPosition(position = 1)
    @Column( name = 'student_id')
    String studentId

    @CsvBindByPosition(position = 0)
    String school

    @CsvBindByPosition(position = 2)
    String grade

    @CsvBindByPosition(position = 3)
    String status

}
