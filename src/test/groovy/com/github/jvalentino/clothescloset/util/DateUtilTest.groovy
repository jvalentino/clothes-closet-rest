package com.github.jvalentino.clothescloset.util

import spock.lang.Specification

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
}
