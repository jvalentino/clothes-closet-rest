package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.dto.OAuthDto
import com.github.jvalentino.clothescloset.dto.ResultDto
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import groovy.transform.CompileDynamic
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

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

    @PostMapping('/login')
    ResultDto login(@Valid @RequestBody OAuthDto oauth) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Arrays.asList(oauth.clientId))
                .build()

        GoogleIdToken idToken = GoogleIdToken.parse(new GsonFactory(), oauth.credential)
        //println idToken.toString()

        if (!idToken.verify(verifier)) {
            return new ResultDto(success:false, messages:['Invalid OAuth'])
        }

        //println idToken.payload.getEmail()
        new ResultDto()
    }

}
