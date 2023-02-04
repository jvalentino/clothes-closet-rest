package com.github.jvalentino.clothescloset.service

import com.github.jvalentino.clothescloset.dto.MakeAppointmentDto
import com.github.jvalentino.clothescloset.dto.MakeAppointmentResultDto
import com.github.jvalentino.clothescloset.dto.ResultDto
import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Student
import com.github.jvalentino.clothescloset.entity.Visit
import com.github.jvalentino.clothescloset.repo.AcceptedIdRepository
import com.github.jvalentino.clothescloset.repo.AppointmentRepository
import com.github.jvalentino.clothescloset.repo.GuardianRepository
import com.github.jvalentino.clothescloset.repo.StudentRepository
import com.github.jvalentino.clothescloset.repo.VisitRepository
import com.github.jvalentino.clothescloset.util.DateUtil
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.servlet.http.HttpServletRequest
import java.sql.Timestamp

/**
 * General service for messing with appointments
 * @author john.valentino
 */
@CompileDynamic
@Service
@Slf4j
@SuppressWarnings([
        'UnnecessaryObjectReferences',
        'UnnecessaryGetter',
        'UnnecessarySetter',
])
class AppointmentService {

    @Autowired
    AppointmentRepository appointmentRepository

    @Autowired
    CalendarService calendarService

    @Autowired
    GuardianRepository guardianRepository

    @Autowired
    AcceptedIdRepository acceptedIdRepository

    @Autowired
    StudentRepository studentRepository

    @Autowired
    VisitRepository visitRepository

    void rescheduleAppointment(Long appointmentId, String datetime, String timeZone) {
        Appointment appointment = appointmentRepository.getAppointmentDetails(appointmentId).first()
        appointment.waitlist = false
        appointment.datetime = DateUtil.isoToTimestamp(datetime, timeZone)
        appointment.year = DateUtil.determineYear(appointment.datetime)
        appointment.semester = DateUtil.determineSemester(appointment.datetime)

        // if there is already an appointment time, delete it
        if (appointment.eventId != null) {
            calendarService.deleteEvent(appointment.eventId)
        }

        MakeAppointmentDto makeAppointment = new MakeAppointmentDto()
        makeAppointment.datetime = datetime
        makeAppointment.timeZone = timeZone
        makeAppointment.guardian = appointment.guardian

        for (Visit visit : appointment.visits) {
            if (visit.student != null) {
                makeAppointment.students.add(visit.student)
            }
        }
        appointment.eventId = calendarService.bookSlot(makeAppointment)

        appointmentRepository.save(appointment)
    }

    MakeAppointmentResultDto schedule(MakeAppointmentDto appointment, HttpServletRequest request,
        boolean override=false) {
        MakeAppointmentResultDto result = new MakeAppointmentResultDto()

        // first check that all student Ids are on the list, unless this is an admin override
        if (!override) {
            this.validateStudentIdsOnList(appointment, result)

            if (result.messages.size() != 0) {
                return result
            }

            // then validate this this time slot is not already booked (if not going onto the wait list)
            List<Appointment> matches = this.findAlreadyBookedSlots(appointment)

            if (matches.size() != 0) {
                return new MakeAppointmentResultDto(success:false, messages:['Already booked'], codes:['BOOKED'])
            }
        }

        // create a new appointment
        Appointment app = this.generateAppointment(appointment, request)

        // now make sure these students have not already had a visit this semester
        if (!override) {
            this.validateStudentsHaveNotAlreadyBeen(appointment, result, app)
            if (result.messages.size() != 0) {
                return result
            }
        }

        // book this time on the calendar if not on the wait list
        if (!appointment.waitlist) {
            String eventId = calendarService.bookSlot(appointment)
            app.eventId = eventId
        }

        // handle the guardian
        guardianRepository.save(appointment.guardian)

        // handle each student
        for (Student student : appointment.students) {
            student = studentRepository.save(student)
            result.studentIds.add(student.studentId)
        }

        app = appointmentRepository.save(app)
        result.appointmentId = app.appointmentId

        // create a visit for each student
        for (Student student : appointment.students) {
            Visit visit = new Visit()
            visit.appointment = app
            visit.student = student
            visit.happened = false
            visit = visitRepository.save(visit)

            result.visitIds.add(visit.visitId)
        }

        result
    }

    protected void validateStudentIdsOnList(MakeAppointmentDto appointment, ResultDto result) {
        for (Student student : appointment.students) {
            boolean found = acceptedIdRepository.existsById(student.studentId)

            if (!found) {
                result.messages.add(student.studentId)
            }
        }

        if (result.messages.size() != 0) {
            result.success = false
            result.codes.add('STUDENT_IDS')
        }
    }

    protected List<Appointment> findAlreadyBookedSlots(MakeAppointmentDto appointment) {
        // if ths is for the waitlist, there is no time slot
        if (appointment.waitlist) {
            return []
        }
        List<Appointment> matches = appointmentRepository.findByDate(
                DateUtil.toDate(appointment.datetime, appointment.timeZone))
        matches
    }

    protected Appointment generateAppointment(MakeAppointmentDto appointment, HttpServletRequest request) {
        Appointment app = new Appointment()
        app.guardian = appointment.guardian
        app.happened = false
        app.notified = false
        app.createdDateTime = new Timestamp(appointment.currentDate.time)
        app.ipAddress = request.getRemoteAddr()
        app.locale = appointment.locale
        app.waitlist = appointment.waitlist

        if (appointment.waitlist) {
            app.datetime = null
            app.year = DateUtil.determineYear(app.createdDateTime)
            app.semester = DateUtil.determineSemester(app.createdDateTime)
        } else {
            app.datetime = new Timestamp(DateUtil.toDate(appointment.datetime).time)
            app.year = DateUtil.determineYear(app.datetime)
            app.semester = DateUtil.determineSemester(app.datetime)
        }

        app
    }

    protected @SuppressWarnings(['NestedForLoop'])
    void validateStudentsHaveNotAlreadyBeen(MakeAppointmentDto appointment, ResultDto result,
                                            Appointment app) {
        List<String> studentIds = []
        for (Student student : appointment.students) {
            studentIds.add(student.studentId)
        }
        //List<Appointment> all = appointmentRepository.all(false)
        List<Appointment> prevAppointments = appointmentRepository.findWithVisitsByStudentIds(
                app.semester, app.year, studentIds
        )

        if (prevAppointments.size() != 0) {
            result.success = false
            result.codes = ['ALREADY_BEEN']
            for (Appointment a : prevAppointments) {
                for (Visit v : a.visits) {
                    if (studentIds.contains(v.student.studentId)) {
                        result.messages.add(v.student.studentId + ' ' +
                                DateUtil.fromDate(new Date(a.datetime.time), appointment.timeZone))
                    }
                }
            }
        }
    }

}
