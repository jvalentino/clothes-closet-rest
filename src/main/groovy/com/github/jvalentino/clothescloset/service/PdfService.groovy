package com.github.jvalentino.clothescloset.service

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
@SuppressWarnings(['UnnecessaryObjectReferences'])
class PdfService {

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

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
        doc.save(byteArrayOutputStream)
        byteArrayOutputStream.close()

        byteArrayOutputStream
    }

}
