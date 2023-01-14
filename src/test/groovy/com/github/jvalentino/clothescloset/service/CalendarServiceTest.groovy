package com.github.jvalentino.clothescloset.service

import com.github.jvalentino.clothescloset.dto.AvailabilityDto
import com.github.jvalentino.clothescloset.dto.EventDto
import com.github.jvalentino.clothescloset.util.DateUtil
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

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

    def "Test findAvailableTimeSlots"() {
        given:
        List<EventDto> events = [
                new EventDto(
                        title:'Unavailable',
                        start:'2023-01-14T10:00:35.867-0600',
                        end:'2023-01-14T08:15:00.000-0600'),
                new EventDto(
                        title:'Unavailable',
                        start:'2023-01-14T16:00:00.000-0600',
                        end:'2023-01-15T08:45:00.000-0600'),
                new EventDto(
                        title:'Unavailable',
                        start:'2023-01-15T10:45:00.000-0600',
                        end:'2023-01-15T12:00:00.000-0600'),
                new EventDto(
                        title:'Appointment',
                        start:'2023-01-14T13:30:00.000-0600',
                        end:'2023-01-14T14:00:00.000-0600'),
        ]
        String timeZone = 'America/Chicago'
        int timeSlotMinutes = 30

        when:
        AvailabilityDto result = subject.findAvailableTimeSlots(events, timeZone, timeSlotMinutes)

        then:
        result.ranges.get(0).startIso == '2023-01-14T08:15:00.000-0600'
        result.ranges.get(0).endIso == '2023-01-14T16:00:00.000-0600'

        result.ranges.get(1).startIso == '2023-01-15T08:45:00.000-0600'
        result.ranges.get(1).endIso == '2023-01-15T10:45:00.000-0600'

        and:
        result.adjustedRanges.get(0).startIso == '2023-01-14T08:30:00.000-0600'
        result.adjustedRanges.get(0).endIso == '2023-01-14T16:00:00.000-0600'

        result.adjustedRanges.get(1).startIso == '2023-01-15T09:00:00.000-0600'
        result.adjustedRanges.get(1).endIso == '2023-01-15T10:30:00.000-0600'

        and: "for the first adjusted time range"
        int index = 0
        result.startDateTimes.get(index++) == '2023-01-14T08:30:00.000-0600'
        result.startDateTimes.get(index++) == '2023-01-14T09:00:00.000-0600'
        result.startDateTimes.get(index++) == '2023-01-14T09:30:00.000-0600'
        result.startDateTimes.get(index++) == '2023-01-14T10:00:00.000-0600'
        result.startDateTimes.get(index++) == '2023-01-14T10:30:00.000-0600'
        result.startDateTimes.get(index++) == '2023-01-14T11:00:00.000-0600'
        result.startDateTimes.get(index++) == '2023-01-14T11:30:00.000-0600'
        result.startDateTimes.get(index++) == '2023-01-14T12:00:00.000-0600'
        result.startDateTimes.get(index++) == '2023-01-14T12:30:00.000-0600'
        result.startDateTimes.get(index++) == '2023-01-14T13:00:00.000-0600'
        result.startDateTimes.get(index++) == '2023-01-14T13:30:00.000-0600'
        result.startDateTimes.get(index++) == '2023-01-14T14:00:00.000-0600'
        result.startDateTimes.get(index++) == '2023-01-14T14:30:00.000-0600'
        result.startDateTimes.get(index++) == '2023-01-14T15:00:00.000-0600'
        result.startDateTimes.get(index++) == '2023-01-14T15:30:00.000-0600'

        and: "for the second adjusted time slot"
        result.startDateTimes.get(index++) == '2023-01-15T09:00:00.000-0600'
        result.startDateTimes.get(index++) == '2023-01-15T09:30:00.000-0600'
        result.startDateTimes.get(index++) == '2023-01-15T10:00:00.000-0600'

        and: "for the actual availabilities for the first time range"
        int index2 = 0
        result.availabilities.get(index2++) == '2023-01-14T08:30:00.000-0600'
        result.availabilities.get(index2++) == '2023-01-14T09:00:00.000-0600'
        result.availabilities.get(index2++) == '2023-01-14T09:30:00.000-0600'
        result.availabilities.get(index2++) == '2023-01-14T10:00:00.000-0600'
        result.availabilities.get(index2++) == '2023-01-14T10:30:00.000-0600'
        result.availabilities.get(index2++) == '2023-01-14T11:00:00.000-0600'
        result.availabilities.get(index2++) == '2023-01-14T11:30:00.000-0600'
        result.availabilities.get(index2++) == '2023-01-14T12:00:00.000-0600'
        result.availabilities.get(index2++) == '2023-01-14T12:30:00.000-0600'
        result.availabilities.get(index2++) == '2023-01-14T13:00:00.000-0600'
        result.availabilities.get(index2++) == '2023-01-14T14:00:00.000-0600'
        result.availabilities.get(index2++) == '2023-01-14T14:30:00.000-0600'
        result.availabilities.get(index2++) == '2023-01-14T15:00:00.000-0600'
        result.availabilities.get(index2++) == '2023-01-14T15:30:00.000-0600'

        and: "for the actual availabilities for the second time range"
        result.availabilities.get(index2++) == '2023-01-15T09:00:00.000-0600'
        result.availabilities.get(index2++) == '2023-01-15T09:30:00.000-0600'
        result.availabilities.get(index2++) == '2023-01-15T10:00:00.000-0600'
    }

    @Unroll
    def "test roundIso at forward #forward with #iso to #output"() {
        when:
        String result = subject.roundIso(iso, 'GMT', 30, forward)

        then:
        result == output

        where:
        iso                             | forward   || output
        '2023-01-14T08:15:00.000+0000'  | false     || '2023-01-14T08:30:00.000+0000'
        '2023-01-14T08:45:00.000+0000'  | false     || '2023-01-14T08:30:00.000+0000'
        '2023-01-14T16:00:00.000+0000'  | false     || '2023-01-14T16:00:00.000+0000'
        '2023-01-15T08:45:00.000+0000'  | true      || '2023-01-15T09:00:00.000+0000'
        '2023-01-15T23:45:00.000+0000'  | true      || '2023-01-16T00:00:00.000+0000'
    }
}
