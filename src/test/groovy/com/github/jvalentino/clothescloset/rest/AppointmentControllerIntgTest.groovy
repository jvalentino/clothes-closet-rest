package com.github.jvalentino.clothescloset.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jvalentino.clothescloset.dto.MakeAppointmentDto
import com.github.jvalentino.clothescloset.dto.MakeAppointmentResultDto
import com.github.jvalentino.clothescloset.dto.ResultDto
import com.github.jvalentino.clothescloset.entity.AcceptedId
import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Guardian
import com.github.jvalentino.clothescloset.entity.Student
import com.github.jvalentino.clothescloset.entity.Visit
import com.github.jvalentino.clothescloset.repo.AcceptedIdRepository
import com.github.jvalentino.clothescloset.repo.AppointmentRepository
import com.github.jvalentino.clothescloset.service.CalendarService
import com.github.jvalentino.clothescloset.util.BaseIntg
import com.github.jvalentino.clothescloset.util.DateUtil
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AppointmentControllerIntgTest extends BaseIntg {

    @Autowired
    MockMvc mvc

    @SpringBean
    CalendarService calendarService = Mock()

    @Autowired
    AcceptedIdRepository acceptedIdRepository

    @PersistenceContext
    private EntityManager entityManager

    def setup() {

    }

    def "test schedule"() {
        given: 'A valid appointment payload'
        Guardian guardian = new Guardian()
        guardian.with {
            email = 'alpha@bravo.com'
            firstName = 'Charlie'
            lastName = 'Delta'
            phoneNumber = '+12223334444'
            phoneTypeLabel = 'mobile'
        }

        Student student = new Student()
        student.with {
            studentId = 'echo'
            grade = '1'
            gender = 'Female'
            school = 'Foxtrot'
        }

        MakeAppointmentDto input = new MakeAppointmentDto()
        input.with {
            datetime = '2022-01-02T00:00:00.000+0000'
            timeZone = 'GMT'
            students = [student]
        }
        input.guardian = guardian

        and: 'That the student is on the approved list'
        acceptedIdRepository.save(new AcceptedId(studentId:student.studentId))

        when: 'POST to /appointment/schedule'
        MvcResult response = mvc.perform(
                post("/appointment/schedule")
                        .content(this.asJsonString(input))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        MakeAppointmentResultDto result = this.toObject(response, MakeAppointmentResultDto)

        then: 'Result POST is successful'
        1 * calendarService.bookSlot(_) >> 'event-id'
        result.success

        when:
        Appointment appointment = entityManager.find(Appointment, result.appointmentId)
        Visit visit = entityManager.find(Visit, result.visitIds.get(0))
        Student foundStudent = entityManager.find(Student, result.studentIds.get(0))

        then:
        appointment
        DateUtil.fromDate(appointment.datetime, input.timeZone) == '2022-01-02T00:00:00.000+0000'
        appointment.semester == 'Spring'
        appointment.locale == 'en'
        appointment.notified == false
        appointment.year == 2022
        appointment.happened == false
        appointment.guardian.email == 'alpha@bravo.com'
        appointment.eventId == 'event-id'
        appointment.createdDateTime != null
        appointment.waitlist == false

        and:
        visit
        visit.appointment.appointmentId == result.appointmentId
        visit.happened == false
        visit.backpacks == null
        visit.coats == null
        visit.shoes == null
        visit.person == null
        visit.misc == null
        visit.student.studentId == student.studentId

        and:
        foundStudent
        foundStudent.studentId == student.studentId
        foundStudent.grade == student.grade
        foundStudent.gender == student.gender
        foundStudent.school == student.school
    }

}
