package com.github.jvalentino.clothescloset.service

import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Visit
import com.github.jvalentino.clothescloset.repo.AppointmentRepository
import com.github.jvalentino.clothescloset.util.DateGenerator
import com.github.jvalentino.clothescloset.util.DateUtil
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

/**
 * Used for sending reminder notifications
 * @author john.valentino
 */
@CompileDynamic
@Service
@Slf4j
@SuppressWarnings([
        'NoJavaUtilDate',
        'DuplicateStringLiteral',
        'UnnecessaryObjectReferences',
        'NestedForLoop',
])
class NotificationService {

    static final String N = '\n'
    static final String DEFAULT_TIME_ZONE = 'America/Chicago'

    @Value('${google.directions.address}')
    String address

    @Value('${google.directions.link}')
    String addressLink

    @Value('${contact.phone}')
    String contactPhone

    @Value('${contact.email}')
    String contactEmail

    @Autowired
    EmailService emailService

    @Autowired
    AppointmentRepository appointmentRepository

    @Autowired
    SmsService smsService

    protected NotificationService instance = this

    // every 10 minutes, but this also runs right on startup as well, nice
    @Scheduled(fixedRate = 600000L)
    void handleNotify() {
        log.info('Notification service running...')
        Date startDate = DateGenerator.date()
        Date endDate = DateUtil.addDays(startDate, 1)

        List<Appointment> appointments = appointmentRepository.findWithVisitsNeedingNotification(
                startDate, endDate)

        log.info("${appointments.size()} appointments require notifications")

        for (Appointment appointment : appointments) {
            instance.sendEmail(appointment)
            instance.sendSms(appointment)
        }
    }

    void sendEmail(Appointment appointment) {
        StringBuilder builder = new StringBuilder()
        String friendlyDateTime = DateUtil.timestampToFriendlyTime(appointment.datetime, DEFAULT_TIME_ZONE)

        String subject = "Reminder: Clothes Closet Appointment ${friendlyDateTime}"

        builder.append("<p>${appointment.guardian.firstName} ${appointment.guardian.lastName},</p>${N}")
        builder.append('<p>')
        builder.append('This is a reminder that you have an appointment at the Clothes Closet')
        builder.append(" at ${friendlyDateTime} for:")
        builder.append('</p>' + N)

        builder.append('<ol>' + N)
        for (Visit visit : appointment.visits) {
            builder.append('<li>' + N)
            builder.append("Student ID: ${visit.student.studentId}, Gender: ${visit.student.gender}, ")
            builder.append("Grade: ${visit.student.grade}, School: ${visit.student.school}${N}")
            builder.append('</li>' + N)
        }
        builder.append('</ol>' + N)

        builder.append('<p>')
        builder.append('Please us this link to find it on the map: ')
        builder.append('<a href="')
        builder.append(addressLink)
        builder.append('" target="_blank">')
        builder.append(address)
        builder.append('</a>')
        builder.append('</p>' + N)

        builder.append('<p>')
        builder.append("If you are still having trouble finding the place, contact us at ${contactPhone}")
        builder.append('</p>' + N)

        try {
            log.info("Notifiying ${appointment.guardian.email}")
            emailService.sendEmail(
                    subject,
                    builder.toString(),
                    appointment.guardian.email
            )
            appointment.notified = true
            appointmentRepository.save(appointment)
        } catch (e) {
            log.error('Great, something went wrong with emailing', e)
        }
    }

    void sendSms(Appointment appointment) {
        if (appointment.guardian.phoneTypeLabel != 'mobile') {
            return
        }

        StringBuilder builder = new StringBuilder()
        String friendlyDateTime = DateUtil.timestampToFriendlyTime(appointment.datetime, DEFAULT_TIME_ZONE)
        builder.append(appointment.guardian.firstName)
        builder.append(' ')
        builder.append(appointment.guardian.lastName)
        builder.append(',\n')

        builder.append('This is a reminder for a Clothes Closet Appointment at ')
        builder.append(friendlyDateTime)
        builder.append(' for ')
        builder.append(appointment.visits.size().toString())
        builder.append(' student(s).\n\n')
        builder.append('Please us this link to find it on the map: ')
        builder.append(addressLink)
        builder.append('\n\n')

        builder.append('If you have trouble finding it or cannot make your appointment, ')
        builder.append('please contact us as soon as possible:\n')
        builder.append(contactPhone)
        builder.append('\n')
        builder.append(contactEmail)

        try {
            log.info("Notifiying ${appointment.guardian.phoneNumber}")
            smsService.send(appointment.guardian.phoneNumber, builder.toString())
            appointment.notified = true
            appointmentRepository.save(appointment)
        } catch (e) {
            log.error('Great, something went wrong with SMS', e)
        }
    }

}
