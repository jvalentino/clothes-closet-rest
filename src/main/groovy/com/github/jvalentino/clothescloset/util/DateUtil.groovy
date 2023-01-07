package com.github.jvalentino.clothescloset.util

import groovy.transform.CompileDynamic

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * The same date utility I write in every system
 * @author john.valentino
 */
@CompileDynamic
@SuppressWarnings(['UnnecessaryGString'])
class DateUtil {

    static Date toDate(String iso) {
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH)
        df1.parse(iso)
    }

    static int getYear(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH)
        dateFormat.format(date).toInteger()
    }

}
