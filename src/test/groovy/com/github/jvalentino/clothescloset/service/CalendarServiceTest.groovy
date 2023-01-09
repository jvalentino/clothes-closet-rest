package com.github.jvalentino.clothescloset.service

import com.github.jvalentino.clothescloset.dto.EventDto
import com.github.jvalentino.clothescloset.util.DateUtil
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import spock.lang.Specification
import spock.lang.Subject

class CalendarServiceTest extends Specification {

    static final String TIME_ZONE = 'GMT'

    @Subject
    CalendarService subject

    def setup() {
        subject = new CalendarService()
    }

    def "test fillCalendar when no events"() {
        given:
        List<Event> events = []
        String timeZone = TIME_ZONE
        Date startDate = DateUtil.toDate('2023-01-01T00:00:00.000+0000')
        Date endDate = DateUtil.toDate('2023-01-10T00:00:00.000+0000')

        when:
        List<EventDto> results = subject.fillCalendar(events, timeZone, startDate, endDate)

        then:
        results.size() == 1

        and:
        EventDto event = results.get(0)
        event.start == '2023-01-01T00:00:00.000+0000'
        event.end == '2023-01-10T00:00:00.000+0000'
        event.title == 'Unavailable'
    }

    def "test fillCalendar"() {
        given:
        Event event1 = new Event()
        event1.with {
            start = DateUtil.isoToEventDateTime('2023-01-10T09:00:00.000-0600')
            end = DateUtil.isoToEventDateTime('2023-01-10T16:00:00.000-0600')
            summary = 'open'
        }

        and:
        Event event2 = new Event()
        event2.with {
            start = DateUtil.isoToEventDateTime('2023-01-10T09:00:00.000-0600')
            end = DateUtil.isoToEventDateTime('2023-01-10T09:30:00.000-0600')
            summary = 'booked'
        }

        and:
        Event event3 = new Event()
        event3.with {
            start = DateUtil.isoToEventDateTime('2023-01-10T12:00:00.000-0600')
            end = DateUtil.isoToEventDateTime('2023-01-10T12:30:00.000-0600')
            summary = 'booked'
        }

        and:
        Event event4 = new Event()
        event4.with {
            start = DateUtil.isoToEventDateTime('2023-01-11T09:00:00.000-0600')
            end = DateUtil.isoToEventDateTime('2023-01-11T16:00:00.000-0600')
            summary = 'open'
        }

        and:
        List<Event> events = [
                event1,
                event2,
                event3,
                event4
        ]

        and:
        String timeZone = 'America/Chicago'
        Date startDate = DateUtil.toDate('2023-01-09T09:02:11.045-0600')
        Date endDate = DateUtil.toDate('2023-04-09T09:02:11.045-0500')

        when:
        List<EventDto> results = subject.fillCalendar(events, timeZone, startDate, endDate)

        then:
        results.get(0).title == 'Unavailable'
        results.get(0).start == '2023-01-09T09:02:11.045-0600'
        results.get(0).end == '2023-01-10T09:00:00.000-0600'

        and:
        results.get(1).title == 'Unavailable'
        results.get(1).start == '2023-01-10T16:00:00.000-0600'
        results.get(1).end == '2023-01-11T09:00:00.000-0600'

        and:
        results.get(2).title == 'Unavailable'
        results.get(2).start == '2023-01-11T16:00:00.000-0600'
        results.get(2).end == '2023-04-09T09:02:11.045-0500'

        and:
        results.get(3).title == 'Appointment'
        results.get(3).start == '2023-01-10T09:00:00.000-0600'
        results.get(3).end == '2023-01-10T09:30:00.000-0600'

        and:
        results.get(4).title == 'Appointment'
        results.get(4).start == '2023-01-10T12:00:00.000-0600'
        results.get(4).end == '2023-01-10T12:30:00.000-0600'
    }
}
