package com.github.jvalentino.clothescloset.service

import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.repo.AppointmentRepository
import com.github.jvalentino.clothescloset.util.DateGenerator
import com.github.jvalentino.clothescloset.util.DateUtil
import spock.lang.Specification
import spock.lang.Subject

class NotificationServiceTest extends Specification {

    @Subject
    NotificationService subject

    def setup() {
        subject = new NotificationService()
        subject.with {
            address = '123 Fake Street'
            addressLink = 'https://123'
            contactPhone = '555-555-5555'
            contactEmail = 'alpha@bravo.com'
            emailService = Mock(EmailService)
            appointmentRepository = Mock(AppointmentRepository)
            smsService = Mock(SmsService)
            instance = Mock(NotificationService)
        }

        GroovyMock(DateGenerator, global:true)
    }

    def "test handleNotify"() {
        given:
        Appointment appointment = new Appointment()
        List<Appointment> appointments = [ appointment ]
        Date startDate = DateUtil.toDate('2022-10-01T00:00:00.000+0000')

        when:
        subject.handleNotify()

        then:
        1 * DateGenerator.date() >> startDate
        1 * subject.appointmentRepository.findWithVisitsNeedingNotification(
                startDate, _) >> appointments
        1 * subject.instance.sendEmail(appointment)
        1 * subject.instance.sendSms(appointment)
    }
}
