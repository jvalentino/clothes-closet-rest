package com.github.jvalentino.clothescloset.util

import spock.lang.Specification
import spock.lang.Unroll

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

    @Unroll
    def "test findSemesterStart where #input is #output"() {
        when:
        Date date = DateUtil.findSemesterStart(DateUtil.toDate(input))
        String result = DateUtil.dateToFriendlyMonthDayYear(date)

        then:
        result == output

        where:
        input                           || output
        '2022-01-01T00:00:00.000+0000'  || '01/01/2022'
        '2022-02-01T00:00:00.000+0000'  || '01/01/2022'
        '2022-03-01T00:00:00.000+0000'  || '01/01/2022'
        '2022-04-01T00:00:00.000+0000'  || '01/01/2022'
        '2021-12-01T00:00:00.000+0000'  || '08/01/2021'
        '2022-05-01T00:00:00.000+0000'  || '01/01/2022'
        '2022-06-01T00:00:00.000+0000'  || '01/01/2022'
        '2022-07-01T00:00:00.000+0000'  || '08/01/2022'
        '2022-08-01T00:00:00.000+0000'  || '08/01/2022'
        '2022-08-01T00:00:00.000+0000'  || '08/01/2022'
        '2022-10-01T00:00:00.000+0000'  || '08/01/2022'
        '2022-11-01T00:00:00.000+0000'  || '08/01/2022'

    }

    @Unroll
    def "test findSemesterEnd where #input is #output"() {
        when:
        Date date = DateUtil.findSemesterEnd(DateUtil.toDate(input))
        String result = DateUtil.dateToFriendlyMonthDayYear(date)

        then:
        result == output

        where:
        input                           || output
        '2022-01-01T00:00:00.000+0000'  || '07/01/2022'
        '2022-02-01T00:00:00.000+0000'  || '07/01/2022'
        '2022-03-01T00:00:00.000+0000'  || '07/01/2022'
        '2022-04-01T00:00:00.000+0000'  || '07/01/2022'
        '2021-12-01T00:00:00.000+0000'  || '12/31/2021'
        '2022-05-01T00:00:00.000+0000'  || '07/01/2022'
        '2022-06-01T00:00:00.000+0000'  || '07/01/2022'
        '2022-07-01T00:00:00.000+0000'  || '12/31/2022'
        '2022-08-01T00:00:00.000+0000'  || '12/31/2022'
        '2022-08-01T00:00:00.000+0000'  || '12/31/2022'
        '2022-10-01T00:00:00.000+0000'  || '12/31/2022'
        '2022-11-01T00:00:00.000+0000'  || '12/31/2022'

    }
}
