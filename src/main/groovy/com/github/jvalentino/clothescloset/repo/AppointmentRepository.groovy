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
            order by appointment.datetime DESC
        ''')
    List<Appointment> all()

    @Query('''
            select distinct appointment from Appointment appointment
            left join fetch appointment.guardian as guardian
            where guardian.firstName like ?1 or guardian.lastName like ?1
            order by appointment.datetime DESC
        ''')
    List<Appointment> listByNameMatch(String name)

    @Query('''
            select distinct appointment from Appointment appointment
            left join fetch appointment.guardian as guardian
            where appointment.datetime >= ?1 and appointment.datetime < ?2
            order by appointment.datetime DESC
        ''')
    List<Appointment> listOnDate(Date startDate, Date endDate)

    @Query('''
            select distinct appointment from Appointment appointment
            left join fetch appointment.guardian as guardian
            where appointment.datetime >= ?1 and appointment.datetime < ?2
            and guardian.firstName like ?3 or guardian.lastName like ?3
            order by appointment.datetime DESC
        ''')
    List<Appointment> listOnDateWithNameMatch(Date startDate, Date endDate, String name)

    @Query('''
            select distinct appointment from Appointment appointment
            left join fetch appointment.guardian as guardian
            left join fetch appointment.visits as visits
            left join fetch visits.student as student
            left join fetch visits.person as person
            where appointment.id = ?1
        ''')
    List<Appointment> getAppointmentDetails(long id)

    @Query('''
            select distinct appointment from Appointment appointment
            left join fetch appointment.guardian as guardian
            where appointment.id = ?1
        ''')
    List<Appointment> getWithGuardian(long id)

    @Query('''
            select distinct appointment from Appointment appointment
            left join fetch appointment.guardian as guardian
            where guardian.email = ?1 and appointment.id != ?2
            order by appointment.datetime DESC
        ''')
    List<Appointment> findForGuardian(String email, Long id)

}
