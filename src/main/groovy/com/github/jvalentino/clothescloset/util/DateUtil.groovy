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
@SuppressWarnings([
        'UnnecessaryGString',
        'UnnecessarySetter',
        'UnnecessaryGetter',
        'NoJavaUtilDate',
        'DuplicateNumberLiteral',
])
class DateUtil {

    static final String ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    static final String GMT = 'GMT'
    static final String YYYY_MM_DD = 'yyyy-MM-dd'
    static final String FRIENDLY = 'EEEEE, MMM d, yyyy \'at\' hh:mm aaa z'
    static final String MM_DD_YYYY = 'MM/dd/yyyy'
    static final String TIME = 'hh:mm aaa z'

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

    static int getYear(Date date, String timeZone=GMT) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH)
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone))
        dateFormat.format(date).toInteger()
    }

    static int getMonth(Date date, String timeZone=GMT) {
        DateFormat dateFormat = new SimpleDateFormat("MM", Locale.ENGLISH)
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone))
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

    static Date addMinutes(Date date, int minutes) {
        Calendar c = Calendar.instance
        c.time = date
        c.add(Calendar.MINUTE, minutes)

        c.time
    }

    static Timestamp isoToTimestamp(String iso, String timeZone=GMT) {
        new Timestamp(DateUtil.toDate(iso, timeZone).time)
    }

    static String timestampToFriendlyTime(Timestamp timestamp, String timeZone=GMT) {
        Date date = new Date(timestamp.time)
        DateFormat dateFormat = new SimpleDateFormat(FRIENDLY, Locale.ENGLISH)
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone))
        dateFormat.format(date)
    }

    static String timestampToFriendlyMonthDayYear(Timestamp timestamp, String timeZone=GMT) {
        Date date = new Date(timestamp.time)
        DateFormat dateFormat = new SimpleDateFormat(MM_DD_YYYY, Locale.ENGLISH)
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone))
        dateFormat.format(date)
    }

    static String dateToFriendlyMonthDayYear(Date date, String timeZone=GMT) {
        DateFormat dateFormat = new SimpleDateFormat(MM_DD_YYYY, Locale.ENGLISH)
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone))
        dateFormat.format(date)
    }

    static String timestampToFriendlyTimeAMPM(Timestamp timestamp, String timeZone=GMT) {
        Date date = new Date(timestamp.time)
        DateFormat dateFormat = new SimpleDateFormat(TIME, Locale.ENGLISH)
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone))
        dateFormat.format(date)
    }

    static String timestampToIso(Timestamp timestamp, String timeZone=GMT) {
        DateUtil.fromDate(new Date(timestamp.time), timeZone)
    }

    static Date findSemesterStart(Date date, String timeZone=GMT) {
        int year = DateUtil.getYear(date, timeZone)
        int month = DateUtil.getMonth(date, timeZone)

        int startMonth = 8
        if (month >= 1 && month <= 6) {
            startMonth = 1
        }

        DateUtil.fromYearMonthDay("${year}-${startMonth}-01", timeZone)
    }

    static Date findSemesterEnd(Date date, String timeZone=GMT) {
        int year = DateUtil.getYear(date, timeZone)
        int month = DateUtil.getMonth(date, timeZone)

        int endMonth = 12
        if (month >= 1 && month <= 6) {
            endMonth = 6
        }

        DateUtil.fromYearMonthDay("${year}-${endMonth}-31", timeZone)
    }

}
