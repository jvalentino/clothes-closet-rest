package com.github.jvalentino.clothescloset.util

import java.text.DateFormat
import java.text.SimpleDateFormat

class DateUtil {

    static Date toDate(String iso) {
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        df1.parse(iso)
    }

    static int getYear(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy")
        dateFormat.format(date).toInteger()
    }

}
