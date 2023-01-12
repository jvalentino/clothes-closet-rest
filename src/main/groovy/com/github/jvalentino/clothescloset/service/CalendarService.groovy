package com.github.jvalentino.clothescloset.service

import com.github.jvalentino.clothescloset.dto.EventDto
import com.github.jvalentino.clothescloset.dto.MakeAppointmentDto
import com.github.jvalentino.clothescloset.entity.Student
import com.github.jvalentino.clothescloset.util.DateUtil
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.services.calendar.model.EventAttendee
import com.google.api.services.calendar.model.EventDateTime
import groovy.transform.CompileDynamic

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import org.springframework.stereotype.Service

/**
 * For dealing with the calendar magic
 * @author john.valentino
 */
@CompileDynamic
@Service
@SuppressWarnings([
        'NoJavaUtilDate',
        'UnnecessaryGetter',
        'UnnecessaryGString',
        'UnnecessarySetter',
        'UnnecessaryPackageReference'])
class CalendarService {

    static final JsonFactory JSON_FACTORY = GsonFactory.defaultInstance
    static final String UNAVAILABLE = 'Unavailable'
    static final String OPEN = 'open'
    static final String COLOR_UNAVAILABLE = '#CCCCCC'
    static final String COLOR_APPOINTMENT = '#ADADAD'
    static final String GOOGLE_CAL_ID = System.getenv('GOOGLE_CAL_ID')
    static final String GOOGLE_CRED_JSON = System.getenv('GOOGLE_CRED_JSON')

    InputStream loadGoogleCredentials() {
        String base64 = GOOGLE_CRED_JSON
        new ByteArrayInputStream(base64.decodeBase64())
    }

    Calendar generateService() {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        Credential credential = this.credentials

        Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName('APPLICATION_NAME')
                .build()

        service
    }

    List<Event> getEvents(Date startDate, Date endDate) {
        Calendar service = this.generateService()

        Events events = service.events().list(GOOGLE_CAL_ID)
                .setMaxResults(2500)
                .setTimeMin(new DateTime(startDate.time))
                .setTimeMax(new DateTime(endDate.time))
                .setOrderBy('startTime')
                .setSingleEvents(true)
                .execute()
        List<Event> items = events.items

        items
    }

    List<EventDto> fillCalendar(List<Event> events, String timeZone, Date startDate, Date endDate) {
        // if there are no events, fill the entire calendar
        if (events.size() == 0) {
            EventDto event = new EventDto()
            event.with {
                start = DateUtil.fromDate(startDate, timeZone)
                end = DateUtil.fromDate(endDate, timeZone)
                title = UNAVAILABLE
                color = COLOR_UNAVAILABLE
            }
            return [event]
        }

        List<EventDto> results = []

        // fill all the times that are not open
        Date lastEndDate = startDate
        for (Event event : events) {
            if (event.getSummary().toLowerCase() != OPEN) {
                continue
            }

            EventDto booked = new EventDto()
            booked.with {
                start = DateUtil.fromDate(lastEndDate, timeZone)
                end = DateUtil.eventDateTimeToIso(event.getStart(), timeZone)
                title = UNAVAILABLE
                color = COLOR_UNAVAILABLE
            }
            results.add(booked)

            lastEndDate = new Date(event.getEnd().getDateTime().getValue())
        }

        // now we have to create a final event from the last time to the end
        EventDto booked = new EventDto()
        booked.with {
            start = DateUtil.fromDate(lastEndDate, timeZone)
            end = DateUtil.fromDate(endDate, timeZone)
            title = UNAVAILABLE
            color = COLOR_UNAVAILABLE
        }
        results.add(booked)

        // now we have to handle actual bookings
        for (Event event : events) {
            if (event.getSummary().toLowerCase() == OPEN) {
                continue
            }

            EventDto appointment = new EventDto()
            appointment.with {
                start = DateUtil.eventDateTimeToIso(event.getStart(), timeZone)
                end = DateUtil.eventDateTimeToIso(event.getEnd(), timeZone)
                title = 'Appointment'
                color = COLOR_APPOINTMENT
            }
            results.add(appointment)
        }

        results
    }

    String bookSlot(MakeAppointmentDto appointment) {
        Calendar service = this.generateService()
        String eventText = "<b>Guardian</b>: ${appointment.guardian.firstName} ${appointment.guardian.lastName}"
        eventText += "(${appointment.guardian.phoneNumber}) <br />"
        eventText += "${appointment.guardian.email} <br />"
        eventText += "<br />"
        eventText += "<b>Students:</b><br />"
        eventText += "<ol>"

        for (Student student : appointment.students) {
            eventText += "<li>${student.id}, ${student.gender}, ${student.grade}, ${student.school} </li>"
        }
        eventText += "</ol>"

        Event event = new Event()
                .setSummary("${appointment.guardian.lastName} (${appointment.students.size()})")
                .setDescription(eventText)

        DateTime startDateTime = DateUtil.isoToDateTime(appointment.datetime)
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone(appointment.timeZone)
        event.setStart(start)

        java.util.Calendar c = java.util.Calendar.instance
        c.time = DateUtil.toDate(appointment.datetime)
        c.add(java.util.Calendar.MINUTE, 30)
        String endDateIso = DateUtil.fromDate(c.time, appointment.timeZone)

        DateTime endDateTime = DateUtil.isoToDateTime(endDateIso)
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone(appointment.timeZone)
        event.setEnd(end)

        /*EventAttendee[] attendees = new EventAttendee[] {
                new EventAttendee().setEmail(appointment.guardian.email)
        };
        event.setAttendees(Arrays.asList(attendees));*/

        event = service.events().insert(GOOGLE_CAL_ID, event).execute()
        event.getId()
    }

    void deleteEvent(String eventId) {
        if (eventId == null) {
            return
        }
        Calendar service = this.generateService()
        service.events().delete(GOOGLE_CAL_ID, eventId).execute()
    }

    Credential getCredentials() {
        GoogleCredential.fromStream(loadGoogleCredentials()).
                createScoped(Collections.singleton(CalendarScopes.CALENDAR))
    }

}
