package com.github.jvalentino.clothescloset.service

import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.mail.javamail.JavaMailSender

import javax.mail.internet.MimeMessage

/**
 * Used for sending emails
 * @author john.valentino
 */
@CompileDynamic
@Service
@Slf4j
class EmailService {

    static final String SMTP_PASSWORD = System.getenv('SMTP_PASSWORD')
    static final String CC_EMAIL = System.getenv('CC_EMAIL')

    @Value('${spring.mail.username}')
    String sender

    @Autowired
    JavaMailSender javaMailSender

    @Async
    void sendEmailAsync(String subject, String body, String to) {
        this.sendEmail(subject, body, to)
    }

    void sendEmail(String subject, String body, String to) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage()
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, 'utf-8')

        // Setting up necessary details
        helper.from = sender
        helper.to = to
        helper.cc = CC_EMAIL
        helper.setText(body, true)
        helper.subject = subject

        //
        JavaMailSenderImpl impl = javaMailSender
        impl.password = SMTP_PASSWORD

        // Sending the mail
        javaMailSender.send(mimeMessage)
    }

}
