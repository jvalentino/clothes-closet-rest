package com.github.jvalentino.clothescloset.repo

import com.github.jvalentino.clothescloset.entity.Gender
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * DAO for Gender
 * @author john.valentino
 */
interface GenderRepository extends JpaRepository<Gender, String> {

    @Query('select distinct gender from Gender gender order by gender.label')
    List<Gender> retrieveAll()

}
