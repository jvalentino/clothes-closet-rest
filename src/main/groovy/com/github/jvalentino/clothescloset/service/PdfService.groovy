package com.github.jvalentino.clothescloset.service

import com.github.jvalentino.clothescloset.dto.PrintAppointmentDto
import com.github.jvalentino.clothescloset.entity.Guardian
import com.github.jvalentino.clothescloset.entity.Settings
import com.github.jvalentino.clothescloset.entity.Visit
import com.github.jvalentino.clothescloset.util.DateUtil
import groovy.transform.CompileDynamic
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.springframework.stereotype.Service

/**
 * Used to handle PDF generation
 * @author john.valentino
 */
@CompileDynamic
@Service
@SuppressWarnings([
        'UnnecessaryObjectReferences',
        'AbcMetric',
        'ParameterCount',
        'MethodSize',
        'DuplicateNumberLiteral',
])
class PdfService {

    static final int PAGE_TOP_Y = 740
    static final int PAGE_LEFT_X = 50
    static final int FONT_SIZE = 10

    ByteArrayOutputStream generateHelloWorld() {
        PDDocument doc = new PDDocument()

        PDPage page = new PDPage()
        doc.addPage(page)

        PDFont font =  PDType1Font.HELVETICA_BOLD

        PDPageContentStream contents = new PDPageContentStream(doc, page)

        contents.beginText()
        contents.setFont(font, 12)
        contents.newLineAtOffset(100, 700)
        contents.showText('Hello World')
        contents.endText()

        contents.close()

        ByteArrayOutputStream byteArrayOutputStream = toByteArrayOutputStream(doc)
        byteArrayOutputStream
    }

    ByteArrayOutputStream toByteArrayOutputStream(PDDocument doc) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        doc.save(byteArrayOutputStream)
        byteArrayOutputStream.close()

        byteArrayOutputStream
    }

    ByteArrayOutputStream generate(List<PrintAppointmentDto> appointments,
                                   String timeZone = 'America/Chicago') {
        PDDocument doc = new PDDocument()

        for (PrintAppointmentDto appointment : appointments) {
            this.addPage(doc, appointment, timeZone)
        }

        ByteArrayOutputStream byteArrayOutputStream = toByteArrayOutputStream(doc)
        byteArrayOutputStream
    }

    PDPage addPage(PDDocument doc, PrintAppointmentDto appointment, String timeZone) {
        PDPage page = new PDPage()
        doc.addPage(page)

        PDPageContentStream stream = new PDPageContentStream(doc, page)

        int xOffsetMajor = 120
        int xOffsetMinor = 60
        int yOffsetMajor = 30
        int x = PAGE_LEFT_X
        int y = PAGE_TOP_Y

        // Guardian
        this.drawText(stream, 'Name:', x, y, true)
        x += xOffsetMinor

        Guardian guardian = appointment.appointment.guardian
        this.drawText(
                stream,
                "${guardian.firstName} ${guardian.lastName}",
                x,
                y)
        x += xOffsetMajor

        this.drawText(stream, 'Email:', x, y, true)
        x += xOffsetMinor

        this.drawText(stream, guardian.email, x, y)
        x += xOffsetMajor

        this.drawText(stream, 'Phone:', x, y, true)
        x += xOffsetMinor

        this.drawText(stream, "${guardian.phoneNumber} (${guardian.phoneTypeLabel})", x, PAGE_TOP_Y)
        x += xOffsetMajor

        // Students/Persons
        y -= yOffsetMajor

        for (Visit visit : appointment.appointment.visits) {
            x = PAGE_LEFT_X

            if (visit.student != null) {
                this.drawText(stream, 'Student ID:', x, y, true)
                x += xOffsetMinor

                this.drawText(
                        stream, "${visit.student.studentId} (${visit.student.gender[0]})",
                        x, y)
                x += xOffsetMajor

                this.drawText(stream, 'Grade:', x, y, true)
                x += xOffsetMinor

                this.drawText(
                        stream, "${visit.student.grade}",
                        x, y)
                x += xOffsetMajor

                this.drawText(stream, 'School:', x, y, true)
                x += xOffsetMinor

                this.drawText(
                        stream, "${visit.student.school}",
                        x, y)
            } else {
                this.drawText(stream, 'Person:', x, y, true)
                x += xOffsetMinor

                this.drawText(stream, visit.person.relation, x, y)
                x += xOffsetMajor
            }

            y -= yOffsetMajor
        }

        // Appointment Details
        x = PAGE_LEFT_X

        this.drawText(stream, 'Appointment Date:', x, y, true)
        x += xOffsetMajor + xOffsetMinor

        String dateString = DateUtil.timestampToFriendlyMonthDayYear(
                appointment.appointment.datetime, timeZone)
        this.drawText(stream, dateString, x, y)
        x += xOffsetMajor + xOffsetMinor

        this.drawText(stream, 'Time:', x, y, true)
        x +=  xOffsetMinor

        String timeString = DateUtil.timestampToFriendlyTimeAMPM(
                appointment.appointment.datetime, timeZone)
        this.drawText(stream, timeString, x, y)

        // First time / last appointment
        y -= yOffsetMajor
        x = PAGE_LEFT_X

        this.drawText(stream, 'First Time?', x, y, true)
        x += xOffsetMajor + xOffsetMinor

        this.drawText(stream, appointment.firstTime ? 'Yes' : 'No', x, y)
        x += xOffsetMajor + xOffsetMinor

        this.drawText(stream, 'Last:', x, y, true)
        x +=  xOffsetMinor

        String lastDateTime = 'N/A'
        if (appointment.lastAppointmentDateIso != null) {
            lastDateTime = DateUtil.timestampToFriendlyMonthDayYear(
                    DateUtil.isoToTimestamp(appointment.lastAppointmentDateIso, timeZone), timeZone)
        }
        this.drawText(stream,
                lastDateTime,
                x, y)

        // Settings
        y -= yOffsetMajor
        y -= yOffsetMajor
        x = PAGE_LEFT_X

        // Girl Student
        int gY = this.drawSettings(stream, x, y, 'Girl Student', appointment.girlSettings)
        x += 250

        // Boy Student
        int bY = this.drawSettings(stream, x, y, 'Boy Student', appointment.girlSettings)

        if (bY < gY) {
            y = bY
        } else {
            y = gY
        }

        // Signature
        y -= yOffsetMajor
        y -= yOffsetMajor
        x = PAGE_LEFT_X

        this.drawText(stream, 'Parent Signature:', x, y, true)
        x +=  xOffsetMajor

        stream.drawLine(x , y, x + xOffsetMajor * 2, y)

        stream.close()
    }

    @SuppressWarnings(['UnusedMethodParameter'])
    protected int drawSettings(PDPageContentStream stream, int x, int y, String title, List<Settings> settings) {
        int xOffsetMajor = 230
        int xOffsetMinor = 170
        int yOffsetMajor = 30
        int yOffsetMinor = 15

        this.drawText(stream, title, x, y, true)
        y -= yOffsetMajor

        for (Settings setting : settings) {
            this.drawText(
                    stream,
                    "${setting.quantity} ${setting.label}",
                    x,
                    y,
                    true)

            stream.drawLine(x + xOffsetMinor , y, x + xOffsetMajor, y)

            y -= yOffsetMinor
        }

        y
    }

    protected void drawText(PDPageContentStream stream,
                       String text, int x, int y,
                       boolean bold=false, int size=FONT_SIZE) {
        stream.setFont(bold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, size)
        stream.beginText()
        stream.setTextTranslation(x, y)
        stream.showText(text)
        stream.endText()
    }

}
