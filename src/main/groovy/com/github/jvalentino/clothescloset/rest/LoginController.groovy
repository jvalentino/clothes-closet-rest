package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.dto.AuthResponseDto
import com.github.jvalentino.clothescloset.dto.OAuthDto
import com.github.jvalentino.clothescloset.dto.ResultDto
import com.github.jvalentino.clothescloset.repo.SpringSessionRepository
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import groovy.transform.CompileDynamic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.BeanIds
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

import javax.annotation.Resource
import javax.servlet.http.HttpSession
import javax.validation.Valid
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory

/**
 * Handles logging in via Google oath
 * @author john.valentino
 */
@CompileDynamic
@RestController
class LoginController {

    @Autowired
    AuthenticationManager authManager

    @Autowired
    SpringSessionRepository springSessionRepository

    @PostMapping('/oauth')
    AuthResponseDto login(@Valid @RequestBody OAuthDto oauth, HttpSession session) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Arrays.asList(oauth.clientId))
                .build()

        GoogleIdToken idToken = GoogleIdToken.parse(new GsonFactory(), oauth.credential)
        //println idToken.toString()

        if (!idToken.verify(verifier)) {
            return new AuthResponseDto(success:false, messages:['Invalid OAuth'])
        }

        //session.setAttribute('user', oauth.credential)
        println 'session ID ' + session.getId() + ' ' + idToken.payload.getEmail()

        UsernamePasswordAuthenticationToken authReq
                = new UsernamePasswordAuthenticationToken(idToken.payload.getEmail(), session.getId());
        Authentication auth = authManager.authenticate(authReq);
        auth.credentials = session.getId()
        SecurityContext sc = SecurityContextHolder.getContext();
        //println 'principal ' + auth.getPrincipal().toString()
        sc.setAuthentication(auth);
        //auth.setAuthenticated(true)

        session.setAttribute("SPRING_SECURITY_CONTEXT", sc);


        println auth.getProperties()

        //println idToken.payload.getEmail()
        new AuthResponseDto(sessionId:session.getId())
    }

}
