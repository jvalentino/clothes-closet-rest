package com.github.jvalentino.clothescloset.service

import com.github.jvalentino.clothescloset.dto.MakeAppointmentDto
import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Guardian
import com.github.jvalentino.clothescloset.entity.Student
import com.github.jvalentino.clothescloset.entity.Visit
import com.github.jvalentino.clothescloset.repo.AppointmentRepository
import com.github.jvalentino.clothescloset.util.DateUtil
import spock.lang.Specification
import spock.lang.Subject

class AppointmentServiceTest extends Specification {

    @Subject
    AppointmentService subject

    def setup() {
        subject = new AppointmentService()
        subject.with {
            calendarService = Mock(CalendarService)
            appointmentRepository = Mock(AppointmentRepository)
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

}
