package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.dto.AddPersonDto
import com.github.jvalentino.clothescloset.dto.AppointmentSearchDto
import com.github.jvalentino.clothescloset.dto.MakeAppointmentDto
import com.github.jvalentino.clothescloset.dto.ResultDto
import com.github.jvalentino.clothescloset.dto.UpdateAppointmentDto
import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Guardian
import com.github.jvalentino.clothescloset.entity.Person
import com.github.jvalentino.clothescloset.entity.Student
import com.github.jvalentino.clothescloset.entity.Visit
import com.github.jvalentino.clothescloset.repo.AppointmentRepository
import com.github.jvalentino.clothescloset.repo.GuardianRepository
import com.github.jvalentino.clothescloset.repo.PersonRepository
import com.github.jvalentino.clothescloset.repo.StudentRepository
import com.github.jvalentino.clothescloset.repo.VisitRepository
import com.github.jvalentino.clothescloset.service.CalendarService
import com.github.jvalentino.clothescloset.util.DateUtil
import spock.lang.Specification
import spock.lang.Subject

class AppointmentControllerTest extends Specification {

    @Subject
    AppointmentController subject

    def setup() {
        subject = new AppointmentController()
        subject.guardianRepository = Mock(GuardianRepository)
        subject.studentRepository = Mock(StudentRepository)
        subject.appointmentRepository = Mock(AppointmentRepository)
        subject.visitRepository = Mock(VisitRepository)
        subject.calendarService = Mock(CalendarService)
        subject.personRepository = Mock(PersonRepository)
    }

    def "test schedule"() {
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
            id = "foxtrot"
            school = "golf"
            gender = "hotel"
            grade = "india"
        }
        appointment.students = [student]

        when:
        ResultDto result = subject.schedule(appointment)

        then:
        1 * subject.guardianRepository.save(appointment.guardian)
        1 * subject.studentRepository.save(student)
        1 * subject.appointmentRepository.save(_) >> { Appointment app ->
            assert app.datetime.time == 1682899200000
            assert app.guardian.email == appointment.guardian.email
            assert app.year == 2023
            assert app.semester == "Spring"
            assert app.happened == false

            return app
        }
        1 * subject.visitRepository.save(_) >> { Visit visit ->
            assert visit.appointment.guardian.email == appointment.guardian.email
            assert visit.student.id == student.id
            assert visit.happened == false

            return visit
        }
        1 * subject.calendarService.bookSlot(appointment)

        and:
        result.success == true
    }

    def "test searchAppointments when no parameters"() {
        given:
        Optional<String> date = GroovyMock()
        Optional<String> name = GroovyMock()
        String timeZone = 'GMT'

        and:
        List<Appointment> appointments = [
                new Appointment(datetime: DateUtil.isoToTimestamp('2023-05-01T00:00:00.000+0000'))
        ]
        when:
        AppointmentSearchDto result = subject.searchAppointments(date, name, timeZone)

        then:
        1 * date.empty >> true
        1 * name.empty >> true
        1 * subject.appointmentRepository.all() >> appointments

        and:
        result.name == null
        result.date == null
        result.endDateIso == null
        result.startDateIso == null
        result.appointments.size() == 1
        result.appointments.get(0).datetimeIso == '2023-05-01T00:00:00.000+0000'

    }

    def "test searchAppointments when name and date"() {
        given:
        Optional<String> date = GroovyMock()
        Optional<String> name = GroovyMock()
        String timeZone = 'GMT'

        and:
        List<Appointment> appointments = [
                new Appointment(datetime: DateUtil.isoToTimestamp('2023-05-01T00:00:00.000+0000'))
        ]
        when:
        AppointmentSearchDto result = subject.searchAppointments(date, name, timeZone)

        then:
        1 * date.empty >> false
        1 * date.get() >> '2023-05-01'
        1 * name.empty >> false
        1 * name.get() >> 'alpha'
        1 * subject.appointmentRepository.listOnDateWithNameMatch(
                _,
                _,
                "%alpha%") >> appointments

        and:
        result.name == 'alpha'
        result.date == '2023-05-01'
        result.endDateIso == '2023-05-02T00:00:00.000+0000'
        result.startDateIso == '2023-05-01T00:00:00.000+0000'
        result.appointments.size() == 1
        result.appointments.get(0).datetimeIso == '2023-05-01T00:00:00.000+0000'
    }

    def "test searchAppointments when name"() {
        given:
        Optional<String> date = GroovyMock()
        Optional<String> name = GroovyMock()
        String timeZone = 'GMT'

        and:
        List<Appointment> appointments = [
                new Appointment(datetime: DateUtil.isoToTimestamp('2023-05-01T00:00:00.000+0000'))
        ]
        when:
        AppointmentSearchDto result = subject.searchAppointments(date, name, timeZone)

        then:
        1 * date.empty >> true
        1 * name.empty >> false
        1 * name.get() >> 'alpha'
        1 * subject.appointmentRepository.listByNameMatch("%alpha%") >> appointments

        and:
        result.name == 'alpha'
        result.date == null
        result.endDateIso == null
        result.startDateIso == null
        result.appointments.size() == 1
        result.appointments.get(0).datetimeIso == '2023-05-01T00:00:00.000+0000'
    }

    def "test searchAppointments when date"() {
        given:
        Optional<String> date = GroovyMock()
        Optional<String> name = GroovyMock()
        String timeZone = 'GMT'

        and:
        List<Appointment> appointments = [
                new Appointment(datetime: DateUtil.isoToTimestamp('2023-05-01T00:00:00.000+0000'))
        ]
        when:
        AppointmentSearchDto result = subject.searchAppointments(date, name, timeZone)

        then:
        1 * date.empty >> false
        1 * date.get() >> '2023-05-01'
        1 * name.empty >> true
        1 * subject.appointmentRepository.listOnDate(_, _) >> appointments

        and:
        result.name == null
        result.date == '2023-05-01'
        result.endDateIso == '2023-05-02T00:00:00.000+0000'
        result.startDateIso == '2023-05-01T00:00:00.000+0000'
        result.appointments.size() == 1
        result.appointments.get(0).datetimeIso == '2023-05-01T00:00:00.000+0000'
    }

    def "test getAppointmentDetails when no result"() {
        given:
        Long id = 1L
        String timeZone = 'GMT'

        when:
        Appointment result = subject.getAppointmentDetails(id, timeZone)

        then:
        1 * subject.appointmentRepository.getAppointmentDetails(id) >> []

        and:
        result.id == null
    }

    def "test getAppointmentDetails when match"() {
        given:
        Long id = 1L
        String timeZone = 'GMT'

        and:
        List<Appointment> appointments = [
                new Appointment(datetime: DateUtil.isoToTimestamp('2023-05-01T00:00:00.000+0000'))
        ]

        when:
        Appointment result = subject.getAppointmentDetails(id, timeZone)

        then:
        1 * subject.appointmentRepository.getAppointmentDetails(id) >> appointments

        and:
        result.datetimeIso == '2023-05-01T00:00:00.000+0000'
    }

    def "test addPersonToAppointment"() {
        given:
        AddPersonDto dto = new AddPersonDto(appointmentId:1L)
        dto.person = new Person(relation:'alpha')

        when:
        ResultDto result = subject.addPersonToAppointment(dto)

        then:
        1 * subject.personRepository.save(dto.person)
        1 * subject.visitRepository.save(_) >> { Visit visit ->
            assert visit.appointment.id == 1L
        }

        and:
        result.success
    }

    def "test cancelAppointment"() {
        given:
        Long id = 1L

        when:
        ResultDto result = subject.cancelAppointment(id)

        then:
        1 * subject.appointmentRepository.deleteById(id)
        result.success
    }

    def "test updateAppointment"() {
        given:
        UpdateAppointmentDto dto = new UpdateAppointmentDto()
        dto.appointmentId = 1L
        dto.visits = []

        and:
        Guardian guardian = new Guardian(email:'alpha@bravo.com')

        Appointment appointment = new Appointment(id:1L)
        appointment.guardian = guardian
        appointment.happened = false
        appointment.datetime = DateUtil.isoToTimestamp('2023-05-01T00:00:00.000+0000')
        appointment.year = 2023
        appointment.semester = 'Spring'

        and:
        Visit visit1 = new Visit(id:2L, happened:false)
        visit1.student = new Student(id:'3')
        visit1.with {
            socks = 10
            underwear = 11
            shoes = 12
            coats = 13
            backpacks = 14
            misc = 15
        }
        dto.visits.add(visit1)

        Optional<Student> optionalStudent = GroovyMock()
        Student originalStudent = new Student(id:'3')

        and:
        Visit visit2 = new Visit(id:4L, happened:false)
        visit2.person = new Person(id:5L)
        visit2.with {
            socks = 20
            underwear = 21
            shoes = 22
            coats = 23
            backpacks = 24
            misc = 25
        }
        dto.visits.add(visit2)

        Optional<Person> optionalPerson = GroovyMock()
        Person originalPerson = new Person(id:5L)

        when:
        ResultDto result = subject.updateAppointment(dto)

        then:
        1 * subject.appointmentRepository.getWithGuardian(1L) >> [appointment]
        1 * subject.appointmentRepository.save(_) >> { Appointment app ->
            assert app.happened == true
        }

        and:
        1 * subject.studentRepository.findById('3') >> optionalStudent
        1 * optionalStudent.get() >> originalStudent
        1 * subject.visitRepository.save(_) >> { Visit visit ->
            assert visit.socks == 10
            assert visit.underwear == 11
            assert visit.shoes == 12
            assert visit.coats == 13
            assert visit.backpacks == 14
            assert visit.misc == 15
            assert visit.happened == true
        }

        and:
        1 * subject.personRepository.findById(5L) >> optionalPerson
        1 * optionalPerson.get() >> originalPerson
        1 * subject.visitRepository.save(_) >> { Visit visit ->
            assert visit.socks == 20
            assert visit.underwear == 21
            assert visit.shoes == 22
            assert visit.coats == 23
            assert visit.backpacks == 24
            assert visit.misc == 25
            assert visit.happened == true
        }

        and:
        result.success

    }

}
