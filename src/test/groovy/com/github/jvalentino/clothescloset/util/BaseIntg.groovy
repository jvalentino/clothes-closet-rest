package com.github.jvalentino.clothescloset.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jvalentino.clothescloset.dto.ResultDto
import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Guardian
import com.github.jvalentino.clothescloset.entity.SpringSession
import com.github.jvalentino.clothescloset.entity.Student
import com.github.jvalentino.clothescloset.entity.Visit
import com.github.jvalentino.clothescloset.repo.AcceptedIdRepository
import com.github.jvalentino.clothescloset.repo.AppointmentRepository
import com.github.jvalentino.clothescloset.repo.GuardianRepository
import com.github.jvalentino.clothescloset.repo.SpringSessionRepository
import com.github.jvalentino.clothescloset.repo.StudentRepository
import com.github.jvalentino.clothescloset.repo.VisitRepository
import com.github.jvalentino.clothescloset.service.CalendarService
import com.github.jvalentino.clothescloset.service.EmailService
import org.junit.jupiter.api.extension.ExtendWith
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * This is a magical class from the land of Narnia that uses a single spring boot testing
 * runtime environment, but that uses an in memory H2 database as opposed to the PostgreSQL
 * one managed via Liquibase. It also runs each test method in its own transactions
 * so that tests can't interfere with one another via the H2 database.
 */
@EnableAutoConfiguration(exclude = [LiquibaseAutoConfiguration])
@ExtendWith(SpringExtension)
@SpringBootTest
@Transactional//(propagation = Propagation.NESTED)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:integration.properties")
abstract class BaseIntg extends Specification {

    @Autowired
    SpringSessionRepository springSessionRepository

    @Autowired
    MockMvc mvc

    @SpringBean
    CalendarService calendarService = Mock()

    @SpringBean
    EmailService emailService = Mock()

    @PersistenceContext
    EntityManager entityManager

    @Autowired
    AppointmentRepository appointmentRepository

    @Autowired
    GuardianRepository guardianRepository

    @Autowired
    AcceptedIdRepository acceptedIdRepository

    @Autowired
    StudentRepository studentRepository

    @Autowired
    VisitRepository visitRepository

    String makeSession() {
        SpringSession session = new SpringSession()
        session.with {
            primaryId = UUID.randomUUID().toString()
            sessionId = UUID.randomUUID().toString()
            principalName = 'mock'
            creationTime = new Date().time
            lastAccessTime = new Date().time
            maxInactiveInterval = 100_000_000L
            expiryTime = new Date().time + 100_000_000L
        }

        springSessionRepository.save(session)

        session.sessionId
    }

    String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper()
            final String jsonContent = mapper.writeValueAsString(obj)
            return jsonContent
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Object toObject(MvcResult response, Class clazz) {
        String json = response.getResponse().getContentAsString()
        new ObjectMapper().readValue(json, clazz)
    }

    Appointment makeAndStoreAppointment(
            String datetimeIso, boolean waitlist, String eventId=null,
            boolean noshow=false, boolean happened=false,
            String firstName='alpha', String lastName='bravo',
            String email='alpha@bravo.com', String phoneNumber='+12223334444',
            String phoneTypeLabel='mobile', String studentId='echo', String grade='1',
            String gender='Female', String school='Foxtrot') {

        Guardian guardian = new Guardian(
                firstName:firstName,
                lastName:lastName,
                email:email,
                phoneNumber:phoneNumber,
                phoneTypeLabel:phoneTypeLabel
        )
        guardian = guardianRepository.save(guardian)

        Student student = new Student(
                studentId:studentId,
                grade:grade,
                gender:gender,
                school:school,
                underwearSize:'L',
                shoeSize:'10'
        )
        student = studentRepository.save(student)

        Appointment appointment = new Appointment(guardian: guardian)
        appointment.happened = happened
        appointment.waitlist = waitlist
        appointment.noshow = noshow
        appointment.year = datetimeIso == null ? null : DateUtil.getYear(DateUtil.toDate(datetimeIso))
        appointment.semester = datetimeIso == null ? null : DateUtil.determineSemester(DateUtil.isoToTimestamp(datetimeIso))
        appointment.datetime = datetimeIso == null ? null : DateUtil.isoToTimestamp(datetimeIso)
        appointment.eventId = eventId
        appointment.createdDateTime = datetimeIso == null ? null : DateUtil.isoToTimestamp(datetimeIso)
        appointment = appointmentRepository.save(appointment)

        Visit visit = new Visit()
        visit.student = student
        visit.appointment = appointment
        visit = visitRepository.save(visit)

        appointment.visits = [visit]
        appointment = appointmentRepository.save(appointment)

        appointment
    }

}
