package com.github.jvalentino.clothescloset.repo

import com.github.jvalentino.clothescloset.entity.Grade
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * DAO for Grade
 * @author john.valentino
 */
interface GradeRepository extends JpaRepository<Grade, String> {

    @Query('select distinct grade from Grade grade order by grade.orderPosition')
    List<Grade> retrieveAll()

}
