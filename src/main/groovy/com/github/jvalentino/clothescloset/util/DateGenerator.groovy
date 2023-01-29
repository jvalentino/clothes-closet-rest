package com.github.jvalentino.clothescloset.util

import groovy.transform.CompileDynamic

/**
 * I made this so that I could mock new Date() stuff
 * @author john.valentino
 */
@SuppressWarnings(['NoJavaUtilDate'])
@CompileDynamic
class DateGenerator {

    static Date date() {
        new Date()
    }

}
