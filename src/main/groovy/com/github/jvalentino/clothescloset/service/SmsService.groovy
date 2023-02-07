package com.github.jvalentino.clothescloset.service

import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Message

/**
 * Service for sending text messages
 * @author john.valentino
 */
@Slf4j
@CompileDynamic
@Service
@SuppressWarnings(['UnnecessaryPackageReference', 'UnnecessaryGetter'])
class SmsService {

    static final String SID = System.getenv('TWILLIO_SID')
    static final String TOKEN = System.getenv('TWILLIO_TOKEN')

    @Value('${sms.from}')
    String from

    String send(String phoneNumber, String text) {
        Twilio.init(SID, TOKEN)
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(phoneNumber),
                new com.twilio.type.PhoneNumber(from),
                text)
                .create()

        message.getSid()
    }

}
