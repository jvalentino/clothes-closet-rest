package com.github.jvalentino.clothescloset.repo

import com.github.jvalentino.clothescloset.entity.Appointment
import org.springframework.data.jpa.repository.JpaRepository

/**
 * DAO for Appointment
 * @author john.valentino
 */
interface AppointmentRepository extends JpaRepository<Appointment, Long> {

}
