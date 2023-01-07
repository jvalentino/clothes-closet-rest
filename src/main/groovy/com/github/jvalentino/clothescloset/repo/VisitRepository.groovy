package com.github.jvalentino.clothescloset.repo

import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Visit
import org.springframework.data.jpa.repository.JpaRepository

interface VisitRepository extends JpaRepository<Visit, Long> {
}
