package com.github.jvalentino.clothescloset.repo

import com.github.jvalentino.clothescloset.entity.Guardian
import org.springframework.data.jpa.repository.JpaRepository

/**
 * DAO for Guardian
 * @author john.valentino
 */
interface GuardianRepository extends JpaRepository<Guardian, String> {

}
