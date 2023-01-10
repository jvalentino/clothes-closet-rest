package com.github.jvalentino.clothescloset.repo

import com.github.jvalentino.clothescloset.entity.SpringSession
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * DAO for Spring session
 * @author john.valentino
 */
interface SpringSessionRepository extends JpaRepository<SpringSession, String> {

    @Query('select distinct session from SpringSession session where session.sessionId = ?1')
    List<SpringSession> selectBySessionId(String sessionId)

}
