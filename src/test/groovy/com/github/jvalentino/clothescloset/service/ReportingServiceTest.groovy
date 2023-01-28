package com.github.jvalentino.clothescloset.service

import com.github.jvalentino.clothescloset.dto.ReportingDto
import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Person
import com.github.jvalentino.clothescloset.entity.Student
import com.github.jvalentino.clothescloset.entity.Visit
import com.github.jvalentino.clothescloset.repo.AppointmentRepository
import com.github.jvalentino.clothescloset.util.DateUtil
import spock.lang.Specification
import spock.lang.Subject

class ReportingServiceTest extends Specification {

    @Subject
    ReportingService subject

    def setup() {
        subject = new ReportingService()
        subject.appointmentRepository = Mock(AppointmentRepository)
    }

    def "test generateReport"() {
        given:
        Date startDate = DateUtil.fromYearMonthDay('2023-01-01')
        Date endDate = DateUtil.fromYearMonthDay('2023-02-01')
        String timeZone = 'GMT'

        and:
        Visit visit1 = this.makeVisit(
                "alpha",
                null,
                1, 2, 3, 4, 5, 6)
        Visit visit2 = this.makeVisit(
                null,
                "bravo",
                10, 20, 30, 40, 50, 60)
        Appointment appointment1 = new Appointment(visits:[visit1, visit2])

        Visit visit3 = this.makeVisit(
                "charlie",
                null,
                100, 200, 300, 400, 500, 600)
        Appointment appointment2 = new Appointment(visits:[visit3])


        List<Appointment> appointments = [appointment1, appointment2]

        when:
        ReportingDto result = subject.generateReport(startDate, endDate, timeZone)

        then:
        1 * subject.appointmentRepository.findWithVisits(_, _) >> appointments

        and:
        result.start == '01/01/2023'
        result.end == '02/01/2023'
        result.totalPeople == 3
        result.students == 2
        result.persons == 1
        result.socks == 111
        result.underwear == 222
        result.shoes == 333
        result.coats == 444
        result.backpacks == 555
        result.misc == 666
        result.total == 2331
    }


    // utilities
    Visit makeVisit(String studentId, String relation, int socks, int underwear, int shoes, int coats,
                    int backpacks, int misc) {
        Visit visit = new Visit(happened:true, socks:socks, underwear:underwear, shoes:shoes, coats:coats,
                backpacks:backpacks, misc:misc)

        if (studentId != null) {
            visit.student = new Student(studentId:studentId)
        } else {
            visit.person = new Person(relation:relation)
        }

        visit
    }
}
