package com.github.jvalentino.clothescloset.repo

import com.github.jvalentino.clothescloset.entity.Appointment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * DAO for Appointment
 * @author john.valentino
 */
interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query('''
            select distinct appointment from Appointment appointment
            left join fetch appointment.guardian
            order by appointment.datetime
        ''')
    List<Appointment> all()

}
