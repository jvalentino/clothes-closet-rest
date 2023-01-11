package com.github.jvalentino.clothescloset.util

import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.EventDateTime
import groovy.transform.CompileDynamic

import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * The same date utility I write in every system
 * @author john.valentino
 */
@CompileDynamic
@SuppressWarnings(['UnnecessaryGString', 'UnnecessarySetter', 'UnnecessaryGetter'])
class DateUtil {

    static final String ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    static final String GMT = 'GMT'
    static final String YYYY_MM_DD = 'yyyy-MM-dd'

    static Date toDate(String iso, String timeZone=GMT) {
        DateFormat df1 = new SimpleDateFormat(ISO, Locale.ENGLISH)
        df1.setTimeZone(TimeZone.getTimeZone(timeZone))
        df1.parse(iso)
    }

    static String fromDate(Date date, String timeZone=GMT) {
        DateFormat dateFormat = new SimpleDateFormat(ISO, Locale.ENGLISH)
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone))
        dateFormat.format(date)
    }

    static int getYear(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH)
        dateFormat.format(date).toInteger()
    }

    static EventDateTime isoToEventDateTime(String iso) {
        new EventDateTime().setDateTime(new DateTime(DateUtil.toDate(iso).time))
    }

    static String eventDateTimeToIso(EventDateTime input, String timeZone=GMT) {
        DateUtil.fromDate(new Date(input.getDateTime().getValue()), timeZone)
    }

    static DateTime isoToDateTime(String iso) {
        new DateTime(DateUtil.toDate(iso).time)
    }

    static Date fromYearMonthDay(String input, String timeZone=GMT) {
        DateFormat df1 = new SimpleDateFormat(YYYY_MM_DD, Locale.ENGLISH)
        df1.setTimeZone(TimeZone.getTimeZone(timeZone))
        df1.parse(input)
    }

    static Date addDays(Date date, int days) {
        Calendar c = Calendar.instance
        c.time = date
        c.add(Calendar.DAY_OF_MONTH, days)

        c.time
    }

    static Timestamp isoToTimestamp(String iso, String timeZone=GMT) {
        new Timestamp(DateUtil.toDate(iso, timeZone).time)
    }

}
