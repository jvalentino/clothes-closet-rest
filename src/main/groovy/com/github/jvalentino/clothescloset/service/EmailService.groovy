package com.github.jvalentino.clothescloset.service

import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Service
import org.springframework.mail.javamail.JavaMailSender

/**
 * Used for sending emails
 * @author john.valentino
 */
@CompileDynamic
@Service
@Slf4j
class EmailService {

    static final String SMTP_PASSWORD = System.getenv('SMTP_PASSWORD')

    @Value('${spring.mail.username}')
    String sender

    @Autowired
    JavaMailSender javaMailSender

    void sendEmail(String subject, String body, String to) {
        SimpleMailMessage mailMessage = new SimpleMailMessage()

        // Setting up necessary details
        mailMessage.from = sender
        mailMessage.to = to
        mailMessage.text = body
        mailMessage.subject = subject

        //
        JavaMailSenderImpl impl = javaMailSender
        impl.password = SMTP_PASSWORD

        // Sending the mail
        javaMailSender.send(mailMessage)
    }

}
