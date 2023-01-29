package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.dto.DashboardLandingDto
import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.util.BaseIntg
import com.github.jvalentino.clothescloset.util.DateGenerator
import com.github.jvalentino.clothescloset.util.DateUtil
import org.springframework.test.web.servlet.MvcResult

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DashboardControllerIntgTest extends BaseIntg {

    def setup() {
        GroovyMock(DateGenerator, global:true)
    }

    def "test landing"() {
        given:
        String sessionId = this.makeSession()
        String timeZone = 'GMT'

        and: 'make an upcoming appointment'
        Appointment upcoming = this.makeAndStoreAppointment(
                '2021-02-01T10:00:00.000+0000',
                false,
                'event-1')

        and: 'make an appointment that requires attention'
        Appointment attention = this.makeAndStoreAppointment(
                '2021-01-01T10:00:00.000+0000',
                false,
                'event-2'
        )

        and: 'make an appointment on the wait list'
        this.makeAndStoreAppointment(
                null,
                true
        )

        when:
        MvcResult response = mvc.perform(
                get("/dashboard?timeZone=${timeZone}").header('x-auth-token', sessionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
        DashboardLandingDto result = this.toObject(response, DashboardLandingDto)

        then:
        1 * DateGenerator.date() >> DateUtil.toDate('2021-02-01T00:00:00.000+0000', timeZone)

        and:
        result.upcomingAppointments.size() == 1
        result.upcomingAppointments.first().datetimeIso == '2021-02-01T10:00:00.000+0000'
        result.upcomingAppointments.first().appointmentId == upcoming.appointmentId

        and:
        result.requireAttention.size() == 1
        result.requireAttention.first().datetimeIso == '2021-01-01T10:00:00.000+0000'
        result.requireAttention.first().appointmentId == attention.appointmentId

        and:
        result.semesterStartDateString == '01/01/2021'
        result.semesterEndDateString == '07/01/2021'
        result.currentDateString == '02/01/2021'
        result.timeZone == timeZone
        result.year == 2021
        result.semester == 'Spring'
        result.onWaitList == 1L
    }
}
