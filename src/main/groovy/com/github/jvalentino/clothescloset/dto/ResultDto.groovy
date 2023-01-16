package com.github.jvalentino.clothescloset.dto

import groovy.transform.CompileDynamic

/**
 * General result DTO
 * @author john.valentino
 */
@CompileDynamic
class ResultDto {

    boolean success = true
    List<String> messages = []
    List<String> codes = []

}
