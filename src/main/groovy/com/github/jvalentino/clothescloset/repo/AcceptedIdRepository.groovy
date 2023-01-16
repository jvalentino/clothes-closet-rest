package com.github.jvalentino.clothescloset.repo

import com.github.jvalentino.clothescloset.entity.AcceptedId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * DAO for AcceptedId
 * @author john.valentino
 */
interface AcceptedIdRepository extends JpaRepository<AcceptedId, String> {

    @Query('select distinct e.school from AcceptedId e order by e.school')
    List<String> findSchools()

}
