package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.dto.ResultDto
import groovy.transform.CompileDynamic
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * A test of protecting an endpoint via a session
 */
@CompileDynamic
@RestController
@EnableWebSecurity
class ProtectedEndpoint {

    @GetMapping('/protected')
    ResultDto getProtected() {
        new ResultDto()
    }

}
