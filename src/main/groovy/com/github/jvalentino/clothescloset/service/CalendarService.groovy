package com.github.jvalentino.clothescloset.service

import com.github.jvalentino.clothescloset.dto.EventDto
import com.github.jvalentino.clothescloset.util.DateUtil
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
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
@SuppressWarnings(['NoJavaUtilDate', 'UnnecessaryGetter'])
class CalendarService {

    static final JsonFactory JSON_FACTORY = GsonFactory.defaultInstance
    static final String UNAVAILABLE = 'Unavailable'
    static final String OPEN = 'open'

    InputStream loadGoogleCredentials() {
        String base64 = System.getenv('GOOGLE_CRED_JSON')
        new ByteArrayInputStream(base64.decodeBase64())
    }

    List<Event> getEvents(Date startDate, Date endDate) {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        Credential credential = this.credentials

        Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                        .setApplicationName('APPLICATION_NAME')
                        .build()

        Events events = service.events().list(System.getenv('GOOGLE_CAL_ID'))
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
            }
            results.add(appointment)
        }

        results
    }

    Credential getCredentials() {
        GoogleCredential.fromStream(loadGoogleCredentials()).
                createScoped(Collections.singleton(CalendarScopes.CALENDAR))
    }

}
