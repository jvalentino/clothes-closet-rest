package com.github.jvalentino.clothescloset

import groovy.transform.CompileDynamic
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

/**
 * Part of the session hacking, we just return the given auth object
 * because the auth itself is handled in the login controller.
 */
@Service
@Configurable
@CompileDynamic
class CustomAuthenticationProvider implements AuthenticationProvider {

    @Override
    Authentication authenticate(Authentication authentication) {
        authentication
    }

    @Override
    boolean supports(Class<?> authentication) {
        (UsernamePasswordAuthenticationToken.isAssignableFrom(authentication))
    }

}
