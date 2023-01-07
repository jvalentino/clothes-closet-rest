package com.github.jvalentino.clothescloset.repo

import com.github.jvalentino.clothescloset.entity.Student
import org.springframework.data.jpa.repository.JpaRepository

interface StudentRepository extends JpaRepository<Student, String> {
}
