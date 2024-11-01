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
            where appointment.waitlist = ?1
            order by appointment.datetime DESC, appointment.createdDateTime ASC
        ''')
    List<Appointment> all(boolean waitlist)

    @Query('''
            select distinct appointment from Appointment appointment
            left join fetch appointment.guardian as guardian
            where (guardian.firstName like ?1 or guardian.lastName like ?1)
            and appointment.waitlist = ?2
            order by appointment.datetime DESC, appointment.createdDateTime ASC
        ''')
    List<Appointment> listByNameMatch(String name, boolean waitlist)

    @Query('''
            select distinct appointment from Appointment appointment
            left join fetch appointment.guardian as guardian
            left join fetch appointment.visits as visits
            left join fetch visits.student as student
            where student.studentId = ?1
        ''')
    List<Appointment> listByStudentIdMatch(String name)

    @Query('''
            select distinct appointment from Appointment appointment
            left join fetch appointment.guardian as guardian
            where appointment.datetime >= ?1 and appointment.datetime < ?2
            and appointment.waitlist = ?3
            order by appointment.datetime DESC, appointment.createdDateTime ASC
        ''')
    List<Appointment> listOnDate(Date startDate, Date endDate, boolean waitlist)

    @Query('''
            select distinct appointment from Appointment appointment
            left join fetch appointment.guardian as guardian
            where appointment.datetime < ?1
                and appointment.waitlist = false
                and appointment.happened = false
                and appointment.noshow = false
            order by appointment.datetime ASC, appointment.createdDateTime ASC
        ''')
    List<Appointment> listRequireAttention(Date currentDate)

    @Query('''
            select distinct appointment from Appointment appointment
            left join fetch appointment.guardian as guardian
            where appointment.datetime >= ?1 and appointment.datetime < ?2
            and (guardian.firstName like ?3 or guardian.lastName like ?3)
            and appointment.waitlist = ?4
            order by appointment.datetime DESC, appointment.createdDateTime ASC
        ''')
    List<Appointment> listOnDateWithNameMatch(Date startDate, Date endDate, String name, boolean waitlist)

    @Query('''
            select distinct appointment from Appointment appointment
            left join fetch appointment.guardian as guardian
            left join fetch appointment.visits as visits
            left join fetch visits.student as student
            left join fetch visits.person as person
            where appointment.appointmentId = ?1
        ''')
    List<Appointment> getAppointmentDetails(long id)

    @Query('''
            select distinct appointment from Appointment appointment
            left join fetch appointment.guardian as guardian
            left join fetch guardian.appointments as gpointments
            left join fetch appointment.visits as visits
            left join fetch visits.student as student
            left join fetch visits.person as person
            where appointment.appointmentId in ?1
            order by appointment.datetime DESC, gpointments.datetime DESC
        ''')
    List<Appointment> getAppointmentDetailsWithGuardianAppts(List<Long> ids)

    @Query('''
            select distinct appointment from Appointment appointment
            left join fetch appointment.guardian as guardian
            where appointment.appointmentId = ?1
        ''')
    List<Appointment> getWithGuardian(long id)

    @Query('''
            select distinct appointment from Appointment appointment
            left join fetch appointment.guardian as guardian
            where guardian.email = ?1 and appointment.appointmentId != ?2
            and appointment.waitlist = false
            order by appointment.datetime DESC
        ''')
    List<Appointment> findForGuardian(String email, Long id)

    @Query('''
            select distinct appointment from Appointment appointment
            where appointment.datetime = ?1 and appointment.waitlist = false
        ''')
    List<Appointment> findByDate(Date date)

    @Query('''
            select distinct a from Appointment a
            left join fetch a.visits as v
            left join fetch v.person
            left join fetch v.student
            where a.happened = true and a.datetime >= ?1 and a.datetime <= ?2
            and a.waitlist = false
        ''')
    List<Appointment> findWithVisits(Date startDate, Date endDate)

    @Query('''
            select distinct a from Appointment a
            left join fetch a.visits as v
            left join fetch a.guardian
            left join fetch v.person
            left join fetch v.student
            where a.happened = false and a.notified = false and a.datetime >= ?1 and a.datetime <= ?2
            and a.waitlist = false
        ''')
    List<Appointment> findWithVisitsNeedingNotification(Date startDate, Date endDate)

    @Query('''
            select distinct a from Appointment a
            left join fetch a.visits as v
            left join fetch v.student as s
            where a.semester = ?1 and a.year = ?2
            and s.studentId in ?3
        ''')
    List<Appointment> findWithVisitsByStudentIds(String semester, int year, List<String> studentIds)

    @Query('select count(a) from Appointment a where a.waitlist = true')
    Long countOnWaitList()

}
