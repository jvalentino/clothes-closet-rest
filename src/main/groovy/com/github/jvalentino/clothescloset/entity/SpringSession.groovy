package com.github.jvalentino.clothescloset.entity

import groovy.transform.CompileDynamic

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Entity for the session
 * @author john.valentino
 */
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

    @Column(name = 'CREATION_TIME')
    Long creationTime

    @Column(name = 'LAST_ACCESS_TIME')
    Long lastAccessTime

    @Column(name = 'MAX_INACTIVE_INTERVAL')
    Long maxInactiveInterval

    @Column(name = 'EXPIRY_TIME')
    Long expiryTime

}
