package com.github.jvalentino.clothescloset.service

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import groovy.transform.CompileDynamic

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import org.springframework.stereotype.Service

/**
 * For dealing with the calendar magic
 * @author john.valentino
 */
@CompileDynamic
@Service
class CalendarService {

    static final JsonFactory JSON_FACTORY = GsonFactory.defaultInstance

    InputStream loadGoogleCredentials() {
        String base64 = System.getenv('GOOGLE_CRED_JSON')
        new ByteArrayInputStream(base64.decodeBase64())
    }

    List<Event> getEvents() {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        Credential credential = this.credentials

        Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                        .setApplicationName('APPLICATION_NAME')
                        .build()

        DateTime now = new DateTime(System.currentTimeMillis())
        Events events = service.events().list(System.getenv('GOOGLE_CAL_ID'))
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy('startTime')
                .setSingleEvents(true)
                .execute()
        List<Event> items = events.items

        items
    }

    Credential getCredentials() {
        GoogleCredential.fromStream(loadGoogleCredentials()).
                createScoped(Collections.singleton(CalendarScopes.CALENDAR))
    }

}
