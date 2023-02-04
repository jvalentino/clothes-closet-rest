package com.github.jvalentino.clothescloset.service

import com.github.jvalentino.clothescloset.dto.MakeAppointmentDto
import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Visit
import com.github.jvalentino.clothescloset.repo.AppointmentRepository
import com.github.jvalentino.clothescloset.util.DateUtil
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * General service for messing with appointments
 * @author john.valentino
 */
@CompileDynamic
@Service
@Slf4j
class AppointmentService {

    @Autowired
    AppointmentRepository appointmentRepository

    @Autowired
    CalendarService calendarService

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

}
