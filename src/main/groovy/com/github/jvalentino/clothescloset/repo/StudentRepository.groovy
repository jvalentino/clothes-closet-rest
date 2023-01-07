package com.github.jvalentino.clothescloset.repo

import com.github.jvalentino.clothescloset.entity.Student
import org.springframework.data.jpa.repository.JpaRepository

/**
 * DAO for student
 * @author john.valentino
 */
interface StudentRepository extends JpaRepository<Student, String> {

}
