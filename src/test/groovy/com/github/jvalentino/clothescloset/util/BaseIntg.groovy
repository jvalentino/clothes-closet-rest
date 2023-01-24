package com.github.jvalentino.clothescloset.util

import com.github.jvalentino.clothescloset.entity.SpringSession
import com.github.jvalentino.clothescloset.repo.SpringSessionRepository
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

/**
 * This is a magical class from the land of Narnia that uses a single spring boot testing
 * runtime environment, but that uses an in memory H2 database as opposed to the PostgreSQL
 * one managed via Liquibase. It also runs each test method in its own transactions
 * so that tests can't interfere with one another via the H2 database.
 */
@EnableAutoConfiguration(exclude = [LiquibaseAutoConfiguration])
@ExtendWith(SpringExtension)
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:integration.properties")
abstract class BaseIntg extends Specification {

    @Autowired
    SpringSessionRepository springSessionRepository

    String makeSession() {
        SpringSession session = new SpringSession()
        session.with {
            primaryId = UUID.randomUUID().toString()
            sessionId = UUID.randomUUID().toString()
            principalName = 'mock'
            creationTime = new Date().time
            lastAccessTime = new Date().time
            maxInactiveInterval = 100_000_000L
            expiryTime = new Date().time + 100_000_000L
        }

        springSessionRepository.save(session)

        session.sessionId
    }

}
