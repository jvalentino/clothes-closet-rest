package com.github.jvalentino.clothescloset.entity

import groovy.transform.CompileDynamic

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@CompileDynamic
@Entity
@Table(name = 'spring_session')
class SpringSession {

    @Id
    @Column(name = 'primary_id')
    String primaryId

    @Column(name = 'session_id')
    String sessionId

    @Column(name = 'principal_name')
    String principalName

}
