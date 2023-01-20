package com.github.jvalentino.clothescloset.util

import spock.lang.Specification

import java.sql.Timestamp

class DateUtilTest extends Specification {

    def "test fromDate"() {
        given:
        Date date = new Date(1)

        when:
        String result = DateUtil.fromDate(date)

        then:
        result == '1970-01-01T00:00:00.001+0000'
    }

    def "test fromDate CST"() {
        given:
        Date date = new Date(1)

        when:
        String result = DateUtil.fromDate(date, 'CST')

        then:
        result == '1969-12-31T18:00:00.001-0600'
    }

    def "test fromYearMonthDay"() {
        given:
        String input = '2023-02-03'

        when:
        Date date = DateUtil.fromYearMonthDay(input)
        String result = DateUtil.fromDate(date)

        then:
        result == '2023-02-03T00:00:00.000+0000'
    }

    def "test timestampToFriendlyTime"() {
        given:
        Timestamp timestamp = DateUtil.isoToTimestamp('2023-02-03T10:30:00.000+0000')

        when:
        String result = DateUtil.timestampToFriendlyTime(timestamp)

        then:
        result == 'Friday, Feb 3, 2023 at 10:30 AM GMT'
    }
}
