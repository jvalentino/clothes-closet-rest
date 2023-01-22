package com.github.jvalentino.clothescloset.dto

import groovy.transform.CompileDynamic

/**
 * Used to request to print multiple appointments
 * @author john.valentino
 */
@CompileDynamic
class MultiPrintRequestDto {

    List<Long> ids = []
    String timeZone = 'America/Chicago'

}
