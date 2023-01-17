package com.github.jvalentino.clothescloset.dto

import groovy.transform.CompileDynamic

/**
 * Response to oauth
 * @author john.valentino
 */
@CompileDynamic
class AuthResponseDto extends ResultDto {

    String sessionId
    String name
    String pictureUrl

}
