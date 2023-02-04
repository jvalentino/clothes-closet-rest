package com.github.jvalentino.clothescloset.service

import com.github.jvalentino.clothescloset.dto.MakeAppointmentDto
import com.github.jvalentino.clothescloset.dto.ResultDto
import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Guardian
import com.github.jvalentino.clothescloset.entity.Student
import com.github.jvalentino.clothescloset.entity.Visit
import com.github.jvalentino.clothescloset.repo.AcceptedIdRepository
import com.github.jvalentino.clothescloset.repo.AppointmentRepository
import com.github.jvalentino.clothescloset.repo.GuardianRepository
import com.github.jvalentino.clothescloset.repo.StudentRepository
import com.github.jvalentino.clothescloset.repo.VisitRepository
import com.github.jvalentino.clothescloset.util.DateUtil
import spock.lang.Specification
import spock.lang.Subject

import javax.servlet.http.HttpServletRequest

class AppointmentServiceTest extends Specification {

    @Subject
    AppointmentService subject

    def setup() {
        subject = new AppointmentService()
        subject.with {
            calendarService = Mock(CalendarService)
            appointmentRepository = Mock(AppointmentRepository)
            guardianRepository = Mock(GuardianRepository)
            acceptedIdRepository = Mock(AcceptedIdRepository)
            studentRepository = Mock(StudentRepository)
            visitRepository = Mock(VisitRepository)
        }
    }

    void "test rescheduleAppointment when from waitlist"() {
        given:
        Long appointmentId = 1L
        String datetime = '2022-10-31T00:00:00.000+0000'
        String timeZone = 'GMT'

        and:
        Appointment appointment = new Appointment(appointmentId: appointmentId)
        appointment.with {
            waitlist = true
            guardian = new Guardian(email:'alpha@bravo.com')
            visits = [
                    new Visit(student:new Student(studentId:'123'))
            ]
        }

        when:
        subject.rescheduleAppointment(appointmentId, datetime, timeZone)

        then:
        1 * subject.appointmentRepository.getAppointmentDetails(appointmentId) >> [appointment]
        0 * subject.calendarService.deleteEvent(_)
        1 * subject.calendarService.bookSlot(_) >> { MakeAppointmentDto makeAppointment ->
            assert makeAppointment.datetime == datetime
            assert makeAppointment.timeZone == timeZone
            assert makeAppointment.guardian.email == appointment.guardian.email
            assert makeAppointment.students.size() == 1
            assert makeAppointment.students.first().studentId == '123'
            return 'event-id'
        }
        1 * subject.appointmentRepository.save(_) >> { Appointment app ->
            assert app.appointmentId == 1L
            assert app.waitlist == false
            assert app.eventId == 'event-id'
            assert app.year == 2022
            assert app.semester == 'Fall'
            assert DateUtil.timestampToIso(app.datetime) == datetime
        }
    }

    void "test rescheduleAppointment when not on wait list"() {
        given:
        Long appointmentId = 1L
        String datetime = '2022-10-31T00:00:00.000+0000'
        String timeZone = 'GMT'

        and:
        Appointment appointment = new Appointment(appointmentId: appointmentId)
        appointment.with {
            waitlist = false
            eventId = 'previous-event-id'
            guardian = new Guardian(email:'alpha@bravo.com')
            visits = [
                    new Visit(student:new Student(studentId:'123'))
            ]
        }

        when:
        subject.rescheduleAppointment(appointmentId, datetime, timeZone)

        then:
        1 * subject.appointmentRepository.getAppointmentDetails(appointmentId) >> [appointment]
        1 * subject.calendarService.deleteEvent('previous-event-id')
        1 * subject.calendarService.bookSlot(_) >> { MakeAppointmentDto makeAppointment ->
            assert makeAppointment.datetime == datetime
            assert makeAppointment.timeZone == timeZone
            assert makeAppointment.guardian.email == appointment.guardian.email
            assert makeAppointment.students.size() == 1
            assert makeAppointment.students.first().studentId == '123'
            return 'event-id'
        }
        1 * subject.appointmentRepository.save(_) >> { Appointment app ->
            assert app.appointmentId == 1L
            assert app.waitlist == false
            assert app.eventId == 'event-id'
            assert app.year == 2022
            assert app.semester == 'Fall'
            assert DateUtil.timestampToIso(app.datetime) == datetime
        }
    }

    def "test schedule"() {
        given:
        MakeAppointmentDto appointment = new MakeAppointmentDto()
        appointment.datetime = '2023-05-01T00:00:00.000+0000'
        appointment.locale = 'fr'

        appointment.guardian = new Guardian()
        appointment.guardian.with {
            email = "alpha"
            firstName = "bravo"
            lastName = "charlie"
            phoneNumber = "delta"
            phoneTypeLabel = "echo"
        }

        Student student = new Student()
        student.with {
            studentId = "foxtrot"
            school = "golf"
            gender = "hotel"
            grade = "india"
        }
        appointment.students = [student]

        and:
        HttpServletRequest request = GroovyMock()

        when:
        ResultDto result = subject.schedule(appointment, request)

        then:
        1 * request.getRemoteAddr() >> '0.0.0.1'
        1 * subject.acceptedIdRepository.existsById(student.studentId) >> true
        1 * subject.appointmentRepository.findByDate(
                DateUtil.toDate(appointment.datetime, appointment.timeZone)) >> []
        1 * subject.appointmentRepository.findWithVisitsByStudentIds(
                "Spring", 2023, ["foxtrot"]) >> []
        1 * subject.guardianRepository.save(appointment.guardian)
        1 * subject.studentRepository.save(student) >> student
        1 * subject.appointmentRepository.save(_) >> { Appointment app ->
            assert app.datetime.time == 1682899200000
            assert app.guardian.email == appointment.guardian.email
            assert app.year == 2023
            assert app.semester == "Spring"
            assert app.happened == false
            assert app.notified == false
            assert app.createdDateTime != null
            assert app.ipAddress == '0.0.0.1'
            assert app.locale == 'fr'

            return app
        }
        1 * subject.visitRepository.save(_) >> { Visit visit ->
            assert visit.appointment.guardian.email == appointment.guardian.email
            assert visit.student.studentId == student.studentId
            assert visit.happened == false

            return visit
        }
        1 * subject.calendarService.bookSlot(appointment)

        and:
        result.success == true
    }

    def "test schedule when student not found"() {
        given:
        MakeAppointmentDto appointment = new MakeAppointmentDto()
        appointment.datetime = '2023-05-01T00:00:00.000+0000'

        appointment.guardian = new Guardian()
        appointment.guardian.with {
            email = "alpha"
            firstName = "bravo"
            lastName = "charlie"
            phoneNumber = "delta"
            phoneTypeLabel = "echo"
        }

        Student student = new Student()
        student.with {
            studentId = "foxtrot"
            school = "golf"
            gender = "hotel"
            grade = "india"
        }
        appointment.students = [student]

        and:
        HttpServletRequest request = GroovyMock()

        when:
        ResultDto result = subject.schedule(appointment, request)

        then:
        1 * subject.acceptedIdRepository.existsById(student.studentId) >> false
        0 * subject.guardianRepository.save(appointment.guardian)
        result.success == false

    }

}
