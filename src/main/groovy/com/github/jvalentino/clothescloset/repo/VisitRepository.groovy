package com.github.jvalentino.clothescloset.repo

import com.github.jvalentino.clothescloset.entity.Visit
import org.springframework.data.jpa.repository.JpaRepository

/**
 * DAO for visit
 * @author john.valentino
 */
interface VisitRepository extends JpaRepository<Visit, Long> {

}
