package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.dto.ResultDto
import com.github.jvalentino.clothescloset.service.EmailService
import groovy.transform.CompileDynamic
import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    EmailService emailService

    @GetMapping('/protected')
    ResultDto getProtected() {
        new ResultDto()
    }

    @GetMapping('/protected/email/test')
    ResultDto sendEmailTest() {
        emailService.sendEmail('Test Subject', '<h1>This is a test</h1><p>hi</p>', 'jvalentino2@gmail.com')
        new ResultDto()
    }

}
