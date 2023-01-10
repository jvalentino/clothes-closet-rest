package com.github.jvalentino.clothescloset.dto

import groovy.transform.CompileDynamic

/**
 * The response from Google OAuth, given to us by a client
 * @author john.valentino
 */
@CompileDynamic
class OAuthDto {

    String clientId
    String credential

}
