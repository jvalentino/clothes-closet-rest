package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.dto.AddPersonDto
import com.github.jvalentino.clothescloset.dto.AppointmentSearchDto
import com.github.jvalentino.clothescloset.dto.AppointmentSettingsDto
import com.github.jvalentino.clothescloset.dto.CalendarBookingDto
import com.github.jvalentino.clothescloset.dto.MakeAppointmentDto
import com.github.jvalentino.clothescloset.dto.MultiPrintRequestDto
import com.github.jvalentino.clothescloset.dto.PrintAppointmentDto
import com.github.jvalentino.clothescloset.dto.ReportingDto
import com.github.jvalentino.clothescloset.dto.ResultDto
import com.github.jvalentino.clothescloset.dto.UpdateAppointmentDto
import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Person
import com.github.jvalentino.clothescloset.entity.Settings
import com.github.jvalentino.clothescloset.entity.Student
import com.github.jvalentino.clothescloset.entity.Visit
import com.github.jvalentino.clothescloset.repo.AcceptedIdRepository
import com.github.jvalentino.clothescloset.repo.AppointmentRepository
import com.github.jvalentino.clothescloset.repo.GenderRepository
import com.github.jvalentino.clothescloset.repo.GradeRepository
import com.github.jvalentino.clothescloset.repo.GuardianRepository
import com.github.jvalentino.clothescloset.repo.PersonRepository
import com.github.jvalentino.clothescloset.repo.PhoneTypeRepository
import com.github.jvalentino.clothescloset.repo.SchoolRepository
import com.github.jvalentino.clothescloset.repo.SettingsRepository
import com.github.jvalentino.clothescloset.repo.StudentRepository
import com.github.jvalentino.clothescloset.repo.VisitRepository
import com.github.jvalentino.clothescloset.service.CalendarService
import com.github.jvalentino.clothescloset.service.PdfService
import com.github.jvalentino.clothescloset.util.DateUtil
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid
import java.sql.Timestamp

/**
 * REST endpoint for dealing wth appointments
 * @author john.valentino
 */
@CompileDynamic
@RestController
@Validated
@SuppressWarnings([
        'OptionalMethodParameter',
        'NoJavaUtilDate',
        'UnnecessaryObjectReferences',
        'UnnecessaryGetter',
        'UnnecessarySetter',
        'DuplicateStringLiteral',
])
@Slf4j
class AppointmentController {

    @Autowired
    GuardianRepository guardianRepository

    @Autowired
    StudentRepository studentRepository

    @Autowired
    AppointmentRepository appointmentRepository

    @Autowired
    VisitRepository visitRepository

    @Autowired
    GradeRepository gradeRepository

    @Autowired
    SchoolRepository schoolRepository

    @Autowired
    GenderRepository genderRepository

    @Autowired
    PhoneTypeRepository phoneTypeRepository

    @Autowired
    CalendarService calendarService

    @Autowired
    PersonRepository personRepository

    @Autowired
    SettingsRepository settingsRepository

    @Autowired
    AcceptedIdRepository acceptedIdRepository

    @Autowired
    PdfService pdfService

    @PostMapping('/appointment/schedule')
    ResultDto schedule(@Valid @RequestBody MakeAppointmentDto appointment, HttpServletRequest request) {
        ResultDto result = new ResultDto()

        // first check that all student Ids are on the list
        this.validateStudentIdsOnList(appointment, result)

        if (result.messages.size() != 0) {
            return result
        }

        // then validate this this time slot is not already booked
        List<Appointment> matches = appointmentRepository.findByDate(
                DateUtil.toDate(appointment.datetime, appointment.timeZone))
        if (matches.size() != 0) {
            return new ResultDto(success:false, messages:['Already booked'], codes:['BOOKED'])
        }

        // create a new appointment
        Appointment app = new Appointment()
        app.guardian = appointment.guardian
        app.datetime = new Timestamp(DateUtil.toDate(appointment.datetime).time)
        app.year = DateUtil.getYear(app.datetime)
        app.happened = false
        app.notified = false
        app.createdDateTime = new Timestamp(new Date().time)
        app.ipAddress = request.getRemoteAddr()
        app.locale = appointment.locale

        if (app.datetime.month >= 0 && app.datetime.month <= 5) {
            app.semester = 'Spring'
        } else {
            app.semester = 'Fall'
        }

        // now make sure these students have not already had a visit this semester
        this.validateStudentsHaveNotAlreadyBeen(appointment, result, app)
        if (result.messages.size() != 0) {
            return result
        }

        // book this time on the calendar
        String eventId = calendarService.bookSlot(appointment)
        app.eventId = eventId

        // handle the guardian
        guardianRepository.save(appointment.guardian)

        // handle each student
        for (Student student : appointment.students) {
            studentRepository.save(student)
        }

        app = appointmentRepository.save(app)

        // create a visit for each student
        for (Student student : appointment.students) {
            Visit visit = new Visit()
            visit.appointment = app
            visit.student = student
            visit.happened = false
            visitRepository.save(visit)
        }

        result
    }

    void validateStudentIdsOnList(MakeAppointmentDto appointment, ResultDto result) {
        for (Student student : appointment.students) {
            boolean found = acceptedIdRepository.existsById(student.studentId)

            if (!found) {
                result.messages.add(student.studentId)
            }
        }

        if (result.messages.size() != 0) {
            result.success = false
            result.codes.add('STUDENT_IDS')
        }
    }

    @SuppressWarnings(['NestedForLoop'])
    void validateStudentsHaveNotAlreadyBeen(MakeAppointmentDto appointment, ResultDto result,
                                            Appointment app) {
        List<String> studentIds = []
        for (Student student : appointment.students) {
            studentIds.add(student.studentId)
        }
        List<Appointment> prevAppointments = appointmentRepository.findWithVisitsByStudentIds(
                app.semester, app.year, studentIds
        )

        if (prevAppointments.size() != 0) {
            result.success = false
            result.codes = ['ALREADY_BEEN']
            for (Appointment a : prevAppointments) {
                for (Visit v : a.visits) {
                    if (studentIds.contains(v.student.studentId)) {
                        result.messages.add(v.student.studentId + ' ' +
                                DateUtil.fromDate(new Date(a.datetime.time), appointment.timeZone))
                    }
                }
            }
        }
    }

    @GetMapping('/appointment/settings')
    AppointmentSettingsDto getSettings(@RequestParam Optional<String> start,
                                       @RequestParam Optional<String> end,
                                       @RequestParam(required = false, defaultValue = 'America/Chicago')
                                       String timeZone) {
        String endDateString = end.empty ? null : end.get()
        String startDateString = start.empty ? null : start.get()

        if (startDateString == null) {
            startDateString = DateUtil.fromDate(new Date(), timeZone)
        }

        Date startDate = DateUtil.toDate(startDateString, timeZone)

        if (endDateString == null) {
            Calendar c = Calendar.instance
            c.time = startDate
            c.add(Calendar.MONTH, 6)
            endDateString = DateUtil.fromDate(c.time, timeZone)
        }

        Date endDate = DateUtil.toDate(endDateString, timeZone)

        AppointmentSettingsDto result = new AppointmentSettingsDto(timeZone:timeZone)
        result.with {
            genders = genderRepository.retrieveAll()
            schools = schoolRepository.retrieveAll()
            grades = gradeRepository.retrieveAll()
            phoneTypes = phoneTypeRepository.retrieveAll()
            startDateIso = startDateString
            endDateIso = endDateString
        }

        CalendarBookingDto booking = calendarService.findAvailablilty(startDate, endDate, timeZone)
        result.events = booking.events
        result.availability = booking.availability

        result
    }

    @GetMapping('/appointment/search')
    AppointmentSearchDto searchAppointments(@RequestParam Optional<String> date,
                                            @RequestParam Optional<String> name,
                                            @RequestParam(required = false, defaultValue = 'America/Chicago')
                                            String timeZone) {
        String dateString = date.empty ? null : date.get()
        String nameString = name.empty ? null : name.get()

        AppointmentSearchDto result = new AppointmentSearchDto()
        result.name = nameString
        result.date = dateString

        if (result.date != null) {
            result.startDate = DateUtil.fromYearMonthDay(result.date, timeZone)
            result.endDate = DateUtil.addDays(result.startDate, 1)
            result.startDateIso = DateUtil.fromDate(result.startDate, timeZone)
            result.endDateIso = DateUtil.fromDate(result.endDate, timeZone)
        }

        if (result.name != null && result.date != null) {
            result.appointments = appointmentRepository.listOnDateWithNameMatch(
                    result.startDate,
                    result.endDate,
                    "%${result.name}%"
            )
        } else if (result.name != null) {
            result.appointments = appointmentRepository.listByNameMatch("%${result.name}%")
        } else if (result.date != null) {
            result.appointments = appointmentRepository.listOnDate(result.startDate, result.endDate)
        } else {
            result.appointments = appointmentRepository.all()
        }

        addIsoToAppointments(result.appointments, timeZone)

        result
    }

    @GetMapping('/appointment/details')
    Appointment getAppointmentDetails(@RequestParam Long id,
                                      @RequestParam(required = false, defaultValue = 'America/Chicago')
                                      String timeZone) {
        List<Appointment> results = appointmentRepository.getAppointmentDetails(id)

        if (results.empty) {
            return new Appointment()
        }

        addIsoToAppointments(results, timeZone)

        results.first()
    }

    void addIsoToAppointments(List<Appointment> appointments, String timeZone) {
        for (Appointment app : appointments) {
            app.datetimeIso = DateUtil.fromDate(new Date(app.datetime.time), timeZone)
        }
    }

    @PostMapping('/appointment/person')
    Visit addPersonToAppointment(@Valid @RequestBody AddPersonDto dto) {
        Person person = dto.person
        personRepository.save(person)

        Visit visit = new Visit()
        visit.appointment = new Appointment(appointmentId:dto.appointmentId)
        visit.person = person
        visitRepository.save(visit)
    }

    @DeleteMapping('/appointment/cancel')
    ResultDto cancelAppointment(@RequestParam Long id) {
        Appointment appointment = appointmentRepository.findById(id).get()
        calendarService.deleteEvent(appointment.eventId)
        appointmentRepository.deleteById(id)

        new ResultDto()
    }

    @PostMapping('/appointment/update')
    ResultDto updateAppointment(@Valid @RequestBody UpdateAppointmentDto dto) {
        Appointment appointment = appointmentRepository.getWithGuardian(dto.appointmentId).first()
        appointment.happened = true

        appointmentRepository.save(appointment)

        for (Visit visit : dto.visits) {
            visit.appointment = appointment
            visit.happened = true

            if (visit.student != null) {
                visit.student = studentRepository.findById(visit.student.studentId).get()
            }

            if (visit.person != null) {
                String relation = visit.person.relation
                visit.person = personRepository.findById(visit.person.personId).get()
                visit.person.relation = relation
            }

            visitRepository.save(visit)
        }

        new ResultDto()
    }

    @GetMapping('/appointment/print')
    PrintAppointmentDto getPrintDetails(@RequestParam Long id,
                                        @RequestParam(required = false, defaultValue = 'America/Chicago')
                                        String timeZone) {
        MultiPrintRequestDto input = new MultiPrintRequestDto(ids:[id], timeZone:timeZone)
        this.getPrintDetails(input).first()
    }

    @PostMapping('/appointment/prints')
    @SuppressWarnings(['NestedForLoop'])
    List<PrintAppointmentDto> getPrintDetails(@Valid @RequestBody MultiPrintRequestDto input) {
        List<PrintAppointmentDto> results = []

        List<Appointment> appointments = appointmentRepository.getAppointmentDetailsWithGuardianAppts(input.ids)
        addIsoToAppointments(appointments, input.timeZone)

        List<Settings> settings = settingsRepository.retrieveAll()

        for (Appointment appointment : appointments) {
            PrintAppointmentDto result = new PrintAppointmentDto(appointment:appointment)

            for (Settings setting : settings) {
                if (setting.gender == 'Male') {
                    result.boySettings.add(setting)
                } else {
                    result.girlSettings.add(setting)
                }
            }

            if (appointment.guardian?.appointments?.size() <= 1) {
                result.firstTime = true
            } else {
                result.previous = appointment.guardian.appointments.toList()
                result.firstTime = false
                addIsoToAppointments(result.previous, input.timeZone)
                result.lastAppointmentDateIso = result.previous.get(1) .datetimeIso
            }

            results.add(result)
        }

        results
    }

    // http://localhost:8080/appointment/pdf/example?x-auth-token=4c4e0a3e-2895-425c-84f7-d5c15279cbcc
    @GetMapping('/appointment/pdf/example')
    void pdfExample(HttpServletResponse response) {
        ByteArrayOutputStream byteArrayOutputStream = pdfService.generateHelloWorld()

        response.setContentType('application/pdf')
        String headerKey = 'Content-Disposition'
        String headerValue = 'attachment; filename=users.pdf'

        response.setHeader(headerKey, headerValue)

        ServletOutputStream out = response.getOutputStream()
        out.write(byteArrayOutputStream.toByteArray())
    }

    @PostMapping('/appointment/pdf')
    void pdf(@Valid @RequestBody MultiPrintRequestDto input, HttpServletResponse response) {
        List<PrintAppointmentDto> appointments = this.getPrintDetails(input)

        ByteArrayOutputStream byteArrayOutputStream = pdfService.generate(appointments, input.timeZone)

        response.setContentType('application/pdf')
        String headerKey = 'Content-Disposition'
        String headerValue = 'attachment; filename=appointments.pdf'

        response.setHeader(headerKey, headerValue)

        ServletOutputStream out = response.getOutputStream()
        out.write(byteArrayOutputStream.toByteArray())
    }

    @GetMapping('/appointment/report')
    @SuppressWarnings(['NestedForLoop', 'UnnecessaryObjectReferences'])
    ReportingDto report(@RequestParam String start, @RequestParam String end) {
        ReportingDto result = new ReportingDto(start:start, end:end)

        Date startDate = DateUtil.fromYearMonthDay(start)
        Date endDate = DateUtil.fromYearMonthDay(end)

        List<Appointment> appointments = appointmentRepository.findWithVisits(startDate, endDate)
        for (Appointment appointment : appointments) {
            result.appointents++

            for (Visit visit : appointment.visits) {
                if (visit.student != null) {
                    result.students++
                } else {
                    result.persons++
                }

                result.totalPeople++

                result.socks += visit.socks
                result.underwear += visit.underwear
                result.shoes += visit.shoes
                result.coats += visit.coats
                result.backpacks += visit.backpacks
                result.misc += visit.misc

                result.total += visit.socks + visit.underwear + visit.shoes +
                        visit.coats + visit.backpacks + visit.misc
            }
        }

        result
    }

}
