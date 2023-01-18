package com.github.jvalentino.clothescloset.service

import com.github.jvalentino.clothescloset.dto.AvailabilityDto
import com.github.jvalentino.clothescloset.dto.CalendarBookingDto
import com.github.jvalentino.clothescloset.dto.EventDto
import com.github.jvalentino.clothescloset.dto.MakeAppointmentDto
import com.github.jvalentino.clothescloset.dto.TimeRangeDto
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
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

/**
 * For dealing with the calendar magic
 * @author john.valentino
 */
@CompileDynamic
@Service
@Slf4j
@SuppressWarnings([
        'NoJavaUtilDate',
        'UnnecessaryGetter',
        'UnnecessaryGString',
        'UnnecessarySetter',
        'UnnecessaryPackageReference',
        'DuplicateNumberLiteral'])
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

            try {
                EventDto appointment = new EventDto()
                appointment.with {
                    start = DateUtil.eventDateTimeToIso(event.getStart(), timeZone)
                    end = DateUtil.eventDateTimeToIso(event.getEnd(), timeZone)
                    title = 'Appointment'
                    color = COLOR_APPOINTMENT
                }
                results.add(appointment)
            } catch (e) {
                log.error("Could not process event ${event.getSummary()}: ${e.message}")
            }
        }

        results
    }

    AvailabilityDto findAvailableTimeSlots(List<EventDto> events, String timeZone, int timeSlotMinutes=30,
        Date currentDateTime=new Date()) {
        AvailabilityDto result = new AvailabilityDto()

        // looking only at Unavailable events, which are in order from earlier to latest
        List<EventDto> unavailableEvents = []
        for (EventDto event : events) {
            if (event.title == UNAVAILABLE) {
                unavailableEvents.add(event)
            }
        }

        // figure out the time range between event i and i + 1
        for (int i = 0; i < unavailableEvents.size(); i++) {
            EventDto event = unavailableEvents.get(i)
            String currentEventEndDateIso = event.end

            // if there is no i + 1, we assume it is the end of the calendar and not available
            if (i == unavailableEvents.size() - 1) {
                break
            }

            String nextEventStartDateIso = unavailableEvents.get(i + 1).start

            result.ranges.add(new TimeRangeDto(startIso:currentEventEndDateIso, endIso:nextEventStartDateIso))
        }

        // now we have to adjust the ranges to align with the time intervals
        for (TimeRangeDto range : result.ranges) {
            result.adjustedRanges.add(new TimeRangeDto(
                    startIso:this.roundIso(range.startIso, timeZone, timeSlotMinutes, true),
                    endIso:this.roundIso(range.endIso, timeZone, timeSlotMinutes, false)))
        }

        // calculate in time slot minutes increments
        for (TimeRangeDto range : result.adjustedRanges) {
            Date startTime = DateUtil.toDate(range.startIso)
            Date endTime = DateUtil.toDate(range.endIso)

            Date currentTime = new Date(startTime.time)
            while (currentTime.time < endTime.time) {
                result.startDateTimes.add(DateUtil.fromDate(currentTime, timeZone))
                currentTime = DateUtil.addMinutes(currentTime, timeSlotMinutes)
            }
        }

        // for every other event that is not unavailable, remove it from the list
        Set<String> bookedTimes = []
        for (EventDto event : events) {
            if (event.title != UNAVAILABLE) {
                bookedTimes.add(event.start)
            }
        }

        for (String available : result.startDateTimes) {
            Date eventTime = DateUtil.toDate(available)

            if (!bookedTimes.contains(available) && eventTime.time > currentDateTime.time) {
                result.availabilities.add(available)
            }
        }

        result
    }

    // TODO: Note that this only works in 30 minute intervals
    String roundIso(String iso, String timeZone, int intervalMinutes=30, boolean forward=false) {
        Date date = DateUtil.toDate(iso, timeZone)

        if (date.minutes > 0 && date.minutes < intervalMinutes) {
            date.minutes = intervalMinutes
        } else if (date.minutes > intervalMinutes) {
            if (forward) {
                date.minutes = 0
                date.hours = date.hours + 1
            } else {
                date.minutes = intervalMinutes
            }
        }

        DateUtil.fromDate(date, timeZone)
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
            eventText += "<li>${student.studentId}, ${student.gender}, ${student.grade}, ${student.school} </li>"
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

    CalendarBookingDto findAvailablilty(Date startDate, Date endDate, String timeZone) {
        CalendarBookingDto result = new CalendarBookingDto()
        result.calendarEvents = this.getEvents(startDate, endDate)
        result.events = this.fillCalendar(result.calendarEvents, timeZone, startDate, endDate)
        result.availability = this.findAvailableTimeSlots(result.events, timeZone, 30)

        result
    }

    Credential getCredentials() {
        GoogleCredential.fromStream(loadGoogleCredentials()).
                createScoped(Collections.singleton(CalendarScopes.CALENDAR))
    }

}
