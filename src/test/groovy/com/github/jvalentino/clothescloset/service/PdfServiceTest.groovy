package com.github.jvalentino.clothescloset.service

import com.github.jvalentino.clothescloset.dto.PrintAppointmentDto
import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Guardian
import com.github.jvalentino.clothescloset.entity.Person
import com.github.jvalentino.clothescloset.entity.Settings
import com.github.jvalentino.clothescloset.entity.Student
import com.github.jvalentino.clothescloset.entity.Visit
import com.github.jvalentino.clothescloset.util.DateUtil
import spock.lang.Specification
import spock.lang.Subject

class PdfServiceTest extends Specification {

    @Subject
    PdfService subject
    File helloFile
    File pdfFile

    def setup() {
        subject = new PdfService()
        new File("build").mkdir()

        helloFile = new File('build/helloworld.pdf')
        helloFile.delete()

        pdfFile = new File('build/example.pdf')
        pdfFile.delete()
    }

    def "test generateHelloWorld"() {
        when:
        ByteArrayOutputStream bos = subject.generateHelloWorld()

        OutputStream outputStream = new FileOutputStream(helloFile)
        bos.writeTo(outputStream)

        then:
        helloFile.exists()
    }

    def "test generate"() {
        given:
        PrintAppointmentDto one = new PrintAppointmentDto(
                firstTime:false, lastAppointmentDateIso:'2023-01-01T00:00:00.000+0000')
        one.girlSettings = [
                new Settings(label:'Bottoms', quantity:3),
                new Settings(label:'Bras', quantity:2)
        ]
        one.boySettings = [
                new Settings(label:'Coats', quantity:4),
                new Settings(label:'Pair Shoes', quantity:1)
        ]
        Appointment a1 = new Appointment()
        a1.with {
            datetime = DateUtil.isoToTimestamp('2023-01-02T00:00:00.000+0000')
            datetimeIso = '2023-01-02T00:00:00.000+0000'
            guardian = new Guardian(
                    firstName:'Alpha',
                    lastName:'Bravo',
                    email:'alpha@bravo.com',
                    phoneNumber:'+12223334444',
                    phoneTypeLabel:'mobile')
            visits = [
                    new Visit(person:new Person(relation:'spouse')),
                    new Visit(student:new Student(studentId:'123', grade:'1', gender:'Male', school:'Charlie School'))
            ]
        }
        one.appointment = a1

        List<PrintAppointmentDto> appointments = [
                one
        ]

        when:
        ByteArrayOutputStream bos = subject.generate(appointments, 'GMT')

        OutputStream outputStream = new FileOutputStream(pdfFile)
        bos.writeTo(outputStream)

        then:
        pdfFile.exists()
    }

}
