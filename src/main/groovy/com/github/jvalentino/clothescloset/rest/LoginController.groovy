package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.dto.AuthResponseDto
import com.github.jvalentino.clothescloset.dto.OAuthDto
import com.github.jvalentino.clothescloset.entity.AuthUser
import com.github.jvalentino.clothescloset.repo.AuthUserRepository
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import groovy.transform.CompileDynamic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

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
@SuppressWarnings(['UnnecessaryGetter', 'UnnecessarySetter'])
class LoginController {

    @Autowired
    AuthenticationManager authManager

    @Autowired
    AuthUserRepository authUserRepository

    @PostMapping('/oauth')
    AuthResponseDto login(@Valid @RequestBody OAuthDto oauth, HttpSession session) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Arrays.asList(oauth.clientId))
                .build()

        GoogleIdToken idToken = GoogleIdToken.parse(new GsonFactory(), oauth.credential)

        if (!idToken.verify(verifier)) {
            return new AuthResponseDto(success:false, messages:['Invalid OAuth'])
        }

        // Need to validate that this user is supported
        List<AuthUser> users = authUserRepository.find(idToken.payload.getEmail())

        if (users.size() == 0) {
            return new AuthResponseDto(success:false, messages:['Not an authorized user'])
        }

        UsernamePasswordAuthenticationToken authReq
                = new UsernamePasswordAuthenticationToken(idToken.payload.getEmail(), session.getId())
        Authentication auth = authManager.authenticate(authReq)
        auth.credentials = session.getId()
        SecurityContext sc = SecurityContextHolder.getContext()
        sc.setAuthentication(auth)

        session.setAttribute('SPRING_SECURITY_CONTEXT', sc)
        session.setMaxInactiveInterval(86400)

        new AuthResponseDto(
                sessionId:session.getId(),
                name:idToken.getPayload().getUnknownKeys().get('name'),
                pictureUrl:idToken.getPayload().getUnknownKeys().get('picture')
        )
    }

}
