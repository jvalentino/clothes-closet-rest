package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.dto.MakeAppointmentDto
import com.github.jvalentino.clothescloset.dto.MakeAppointmentResultDto
import com.github.jvalentino.clothescloset.dto.MoveFromWaitListDto
import com.github.jvalentino.clothescloset.entity.AcceptedId
import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Guardian
import com.github.jvalentino.clothescloset.entity.Student
import com.github.jvalentino.clothescloset.entity.Visit

import com.github.jvalentino.clothescloset.util.BaseIntg
import com.github.jvalentino.clothescloset.util.DateUtil
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AppointmentControllerIntgTest extends BaseIntg {

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
        MakeAppointmentResultDto result = this.scheduleAppointment(input)

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

    def "test schedule on waitlist"() {
        given: 'A valid appointment payload'
        MakeAppointmentDto input = this.generateAppointmentForWaitlist()

        and: 'That the student is on the approved list'
        acceptedIdRepository.save(new AcceptedId(studentId:input.students.first().studentId))

        when: 'POST to /appointment/schedule'
        MakeAppointmentResultDto result = this.scheduleAppointment(input)

        then: 'Result POST is successful'
        0 * calendarService.bookSlot(_)
        result.success

        when:
        Appointment appointment = entityManager.find(Appointment, result.appointmentId)
        Visit visit = entityManager.find(Visit, result.visitIds.get(0))
        Student foundStudent = entityManager.find(Student, result.studentIds.get(0))

        then:
        appointment
        appointment.datetime == null
        appointment.semester == 'Fall'
        appointment.locale == 'en'
        appointment.notified == false
        appointment.year == 2022
        appointment.happened == false
        appointment.guardian.email == 'alpha@bravo.com'
        appointment.eventId == null
        appointment.createdDateTime != null
        appointment.waitlist == true

        and:
        visit
        visit.appointment.appointmentId == result.appointmentId
        visit.happened == false
        visit.backpacks == null
        visit.coats == null
        visit.shoes == null
        visit.person == null
        visit.misc == null
        visit.student.studentId == input.students.first().studentId

        and:
        foundStudent
        foundStudent.studentId == input.students.first().studentId
        foundStudent.grade == input.students.first().grade
        foundStudent.gender == input.students.first().gender
        foundStudent.school == input.students.first().school
    }

    def "test moveFromWaitList"() {
        given: 'we have an authenticated session'
        String sessionId = this.makeSession()

        and: 'We have an existing appointment'
        Appointment appointment = this.makeAndStoreAppointment(null, true)

        and:
        MoveFromWaitListDto input = new MoveFromWaitListDto()
        input.with {
            datetime = '2021-10-31T00:00:00.000+0000'
            timeZone = 'GMT'
            appointmentId = appointment.appointmentId
        }

        when:
        MvcResult response = mvc.perform(
                post("/appointment/waitlist/move")
                        .header('x-auth-token', sessionId)
                        .content(this.asJsonString(input))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        MakeAppointmentResultDto result = this.toObject(response, MakeAppointmentResultDto)

        then:
        1 * calendarService.bookSlot(_) >> { MakeAppointmentDto dto ->
            assert dto.guardian.email == appointment.guardian.email
            assert dto.students.first().studentId == appointment.visits.first().student.studentId
            assert dto.timeZone == input.timeZone
            assert dto.datetime == input.datetime

            return 'event-id'
        }

        result.success

        when:
        Appointment foundAppointment = entityManager.find(Appointment, input.appointmentId)

        then:
        foundAppointment
        foundAppointment.eventId == 'event-id'
        DateUtil.fromDate(new Date(foundAppointment.datetime.time), input.timeZone) == input.datetime
        foundAppointment.waitlist == false
        foundAppointment.semester == 'Fall'
        foundAppointment.year == 2021
    }

    //

    private generateAppointmentForWaitlist() {
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
            datetime = null
            timeZone = 'GMT'
            waitlist = true
            currentDate = DateUtil.toDate('2022-09-01T00:00:00.000+0000', 'GMT')
            students = [student]
        }
        input.guardian = guardian

        input
    }

    private MakeAppointmentResultDto scheduleAppointment(MakeAppointmentDto input) {
        MvcResult response = mvc.perform(
                post("/appointment/schedule")
                        .content(this.asJsonString(input))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        MakeAppointmentResultDto result = this.toObject(response, MakeAppointmentResultDto)
        result
    }

}
