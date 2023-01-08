package com.github.jvalentino.clothescloset.repo

import com.github.jvalentino.clothescloset.entity.School
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * DAO for School
 * @author john.valentino
 */
interface SchoolRepository extends JpaRepository<School, String> {

    @Query('select distinct school from School school order by school.label')
    List<School> retrieveAll()

}
