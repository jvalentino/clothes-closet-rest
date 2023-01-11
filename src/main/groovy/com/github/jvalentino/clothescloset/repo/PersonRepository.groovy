package com.github.jvalentino.clothescloset.repo

import com.github.jvalentino.clothescloset.entity.Person
import org.springframework.data.jpa.repository.JpaRepository

/**
 * DAO for Person
 * @author john.valentino
 */
interface PersonRepository extends JpaRepository<Person, String> {

}
