package com.github.jvalentino.clothescloset

import org.springframework.beans.factory.annotation.Configurable
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
@Configurable
class CustomAuthenticationProvider implements AuthenticationProvider    {
    @Override
    public Authentication authenticate(Authentication authentication) {
        // Your code of custom Authentication

        return authentication

    }

    @Override
    boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
