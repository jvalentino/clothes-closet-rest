package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.dto.AddPersonDto
import com.github.jvalentino.clothescloset.dto.AppointmentSearchDto
import com.github.jvalentino.clothescloset.dto.MakeAppointmentDto
import com.github.jvalentino.clothescloset.dto.PrintAppointmentDto
import com.github.jvalentino.clothescloset.dto.ReportingDto
import com.github.jvalentino.clothescloset.dto.ResultDto
import com.github.jvalentino.clothescloset.dto.UpdateAppointmentDto
import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Guardian
import com.github.jvalentino.clothescloset.entity.Person
import com.github.jvalentino.clothescloset.entity.Settings
import com.github.jvalentino.clothescloset.entity.Student
import com.github.jvalentino.clothescloset.entity.Visit
import com.github.jvalentino.clothescloset.repo.AcceptedIdRepository
import com.github.jvalentino.clothescloset.repo.AppointmentRepository
import com.github.jvalentino.clothescloset.repo.GuardianRepository
import com.github.jvalentino.clothescloset.repo.PersonRepository
import com.github.jvalentino.clothescloset.repo.SettingsRepository
import com.github.jvalentino.clothescloset.repo.StudentRepository
import com.github.jvalentino.clothescloset.repo.VisitRepository
import com.github.jvalentino.clothescloset.service.CalendarService
import com.github.jvalentino.clothescloset.util.DateUtil
import spock.lang.Specification
import spock.lang.Subject

import javax.servlet.http.HttpServletRequest

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
        subject.settingsRepository = Mock(SettingsRepository)
        subject.acceptedIdRepository = Mock(AcceptedIdRepository)
    }

    def "test schedule"() {
        given:
        MakeAppointmentDto appointment = new MakeAppointmentDto()
        appointment.datetime = '2023-05-01T00:00:00.000+0000'
        appointment.locale = 'fr'

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
            studentId = "foxtrot"
            school = "golf"
            gender = "hotel"
            grade = "india"
        }
        appointment.students = [student]

        and:
        HttpServletRequest request = GroovyMock()

        when:
        ResultDto result = subject.schedule(appointment, request)

        then:
        1 * request.getRemoteAddr() >> '0.0.0.1'
        1 * subject.acceptedIdRepository.existsById(student.studentId) >> true
        1 * subject.appointmentRepository.findByDate(
                DateUtil.toDate(appointment.datetime, appointment.timeZone)) >> []
        1 * subject.appointmentRepository.findWithVisitsByStudentIds(
                "Spring", 2023, ["foxtrot"]) >> []
        1 * subject.guardianRepository.save(appointment.guardian)
        1 * subject.studentRepository.save(student) >> student
        1 * subject.appointmentRepository.save(_) >> { Appointment app ->
            assert app.datetime.time == 1682899200000
            assert app.guardian.email == appointment.guardian.email
            assert app.year == 2023
            assert app.semester == "Spring"
            assert app.happened == false
            assert app.notified == false
            assert app.createdDateTime != null
            assert app.ipAddress == '0.0.0.1'
            assert app.locale == 'fr'

            return app
        }
        1 * subject.visitRepository.save(_) >> { Visit visit ->
            assert visit.appointment.guardian.email == appointment.guardian.email
            assert visit.student.studentId == student.studentId
            assert visit.happened == false

            return visit
        }
        1 * subject.calendarService.bookSlot(appointment)

        and:
        result.success == true
    }

    def "test schedule when student not found"() {
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
            studentId = "foxtrot"
            school = "golf"
            gender = "hotel"
            grade = "india"
        }
        appointment.students = [student]

        and:
        HttpServletRequest request = GroovyMock()

        when:
        ResultDto result = subject.schedule(appointment, request)

        then:
        1 * subject.acceptedIdRepository.existsById(student.studentId) >> false
        0 * subject.guardianRepository.save(appointment.guardian)
        result.success == false

    }

    def "test searchAppointments when no parameters"() {
        given:
        Optional<String> date = GroovyMock()
        Optional<String> name = GroovyMock()
        Optional<String> waiting = GroovyMock()
        String timeZone = 'GMT'

        and:
        List<Appointment> appointments = [
                new Appointment(datetime: DateUtil.isoToTimestamp('2023-05-01T00:00:00.000+0000'))
        ]
        when:
        AppointmentSearchDto result = subject.searchAppointments(date, name, waiting, timeZone)

        then:
        1 * date.empty >> true
        1 * name.empty >> true
        1 * waiting.empty >> true
        1 * subject.appointmentRepository.all(false) >> appointments

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
        Optional<String> waiting = GroovyMock()
        String timeZone = 'GMT'

        and:
        List<Appointment> appointments = [
                new Appointment(datetime: DateUtil.isoToTimestamp('2023-05-01T00:00:00.000+0000'))
        ]
        when:
        AppointmentSearchDto result = subject.searchAppointments(date, name, waiting, timeZone)

        then:
        1 * waiting.empty >> true
        1 * date.empty >> false
        1 * date.get() >> '2023-05-01'
        1 * name.empty >> false
        1 * name.get() >> 'alpha'
        1 * subject.appointmentRepository.listOnDateWithNameMatch(
                _,
                _,
                "%alpha%",
                false) >> appointments

        and:
        result.waitlist == false
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
        Optional<String> waiting = GroovyMock()
        String timeZone = 'GMT'

        and:
        List<Appointment> appointments = [
                new Appointment(datetime: DateUtil.isoToTimestamp('2023-05-01T00:00:00.000+0000'))
        ]
        when:
        AppointmentSearchDto result = subject.searchAppointments(date, name, waiting, timeZone)

        then:
        1 * date.empty >> true
        1 * name.empty >> false
        1 * name.get() >> 'alpha'
        1 * waiting.empty >> true
        1 * subject.appointmentRepository.listByNameMatch("%alpha%", false) >> appointments

        and:
        result.name == 'alpha'
        result.date == null
        result.endDateIso == null
        result.startDateIso == null
        result.appointments.size() == 1
        result.appointments.get(0).datetimeIso == '2023-05-01T00:00:00.000+0000'
        result.waitlist == false
    }

    def "test searchAppointments when date"() {
        given:
        Optional<String> date = GroovyMock()
        Optional<String> name = GroovyMock()
        Optional<String> waiting = GroovyMock()
        String timeZone = 'GMT'

        and:
        List<Appointment> appointments = [
                new Appointment(datetime: DateUtil.isoToTimestamp('2023-05-01T00:00:00.000+0000'))
        ]
        when:
        AppointmentSearchDto result = subject.searchAppointments(date, name, waiting, timeZone)

        then:
        1 * date.empty >> false
        1 * date.get() >> '2023-05-01'
        1 * name.empty >> true
        1 * subject.appointmentRepository.listOnDate(_, _, false) >> appointments

        and:
        result.name == null
        result.date == '2023-05-01'
        result.endDateIso == '2023-05-02T00:00:00.000+0000'
        result.startDateIso == '2023-05-01T00:00:00.000+0000'
        result.appointments.size() == 1
        result.appointments.get(0).datetimeIso == '2023-05-01T00:00:00.000+0000'
        result.waitlist == false
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
        result.appointmentId == null
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
        Visit result = subject.addPersonToAppointment(dto)

        then:
        1 * subject.personRepository.save(dto.person)
        1 * subject.visitRepository.save(_) >> { Visit visit ->
            visit.visitId = 123L
            assert visit.appointment.appointmentId == 1L
            visit
        }

        and:
        result.visitId == 123L
        result.person.relation == 'alpha'
    }

    def "test cancelAppointment"() {
        given:
        Long id = 1L
        Optional<Appointment> optional = GroovyMock()
        Appointment appointment = new Appointment(eventId:'alpha')

        when:
        ResultDto result = subject.cancelAppointment(id)

        then:
        1 * subject.appointmentRepository.findById(id) >> optional
        1 * optional.get() >> appointment
        1 * subject.calendarService.deleteEvent('alpha')
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

        Appointment appointment = new Appointment(appointmentId: 1L)
        appointment.guardian = guardian
        appointment.happened = false
        appointment.datetime = DateUtil.isoToTimestamp('2023-05-01T00:00:00.000+0000')
        appointment.year = 2023
        appointment.semester = 'Spring'

        and:
        Visit visit1 = new Visit(visitId:2L, happened:false)
        visit1.student = new Student(studentId:'3')
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
        Student originalStudent = new Student(studentId:'3')

        and:
        Visit visit2 = new Visit(visitId:4L, happened:false,)
        visit2.person = new Person(personId:5L,  relation:'mother')
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
        Person originalPerson = new Person(personId:5L)

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
            assert visit.person.relation == 'mother'
        }

        and:
        result.success
    }

    def "test getPrintDetails when first time"() {
        given:
        Long id = 1L
        String timeZone = 'GMT'

        and:
        Appointment appointment = new Appointment(appointmentId: 1L)
        appointment.datetime = DateUtil.isoToTimestamp('2023-01-02T00:00:00.000+0000')
        appointment.guardian = new Guardian(email:'charlie')

        and:
        List<Settings> settings = [
                new Settings(gender:'Male', label:'alpha'),
                new Settings(gender:'Female', label:'bravo'),
        ]

        when:
        PrintAppointmentDto result = subject.getPrintDetails(id, timeZone)

        then:
        1 * subject.appointmentRepository.getAppointmentDetailsWithGuardianAppts([id]) >> [appointment]
        1 * subject.settingsRepository.retrieveAll() >> settings

        and:
        result.firstTime == true
        result.lastAppointmentDateIso == null
        result.appointment.appointmentId == appointment.appointmentId
        result.appointment.datetimeIso == '2023-01-02T00:00:00.000+0000'
        result.boySettings.size() == 1
        result.girlSettings.size() == 1
        result.boySettings.get(0).label == 'alpha'
        result.girlSettings.get(0).label == 'bravo'
        result.previous.size() == 0
    }

    def "test getPrintDetails when NOT first time"() {
        given:
        Long id = 1L
        String timeZone = 'GMT'

        and:
        Appointment appointment = new Appointment(appointmentId: 1L)
        appointment.datetime = DateUtil.isoToTimestamp('2023-01-02T00:00:00.000+0000')
        appointment.guardian = new Guardian(email:'charlie')

        and:
        List<Settings> settings = [
                new Settings(gender:'Male', label:'alpha'),
                new Settings(gender:'Female', label:'bravo'),
        ]

        and:
        appointment.guardian.appointments = [
                new Appointment(datetime:DateUtil.isoToTimestamp('2023-01-02T00:00:00.000+0000')),
                new Appointment(datetime:DateUtil.isoToTimestamp('2023-01-01T00:00:00.000+0000'))
        ]

        when:
        PrintAppointmentDto result = subject.getPrintDetails(id, timeZone)

        then:
        1 * subject.appointmentRepository.getAppointmentDetailsWithGuardianAppts([id]) >> [appointment]
        1 * subject.settingsRepository.retrieveAll() >> settings

        and:
        result.firstTime == false
        result.lastAppointmentDateIso == '2023-01-01T00:00:00.000+0000'
        result.appointment.appointmentId == appointment.appointmentId
        result.appointment.datetimeIso == '2023-01-02T00:00:00.000+0000'
        result.boySettings.size() == 1
        result.girlSettings.size() == 1
        result.boySettings.get(0).label == 'alpha'
        result.girlSettings.get(0).label == 'bravo'
        result.previous.size() == 2
        result.previous.get(1).datetimeIso == '2023-01-01T00:00:00.000+0000'
    }

    def "test report"() {
        given:
        String start = '2023-01-01'
        String end = '2023-02-01'

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
        ReportingDto result = subject.report(start, end)

        then:
        1 * subject.appointmentRepository.findWithVisits(_, _) >> appointments

        and:
        result.start == start
        result.end == end
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
