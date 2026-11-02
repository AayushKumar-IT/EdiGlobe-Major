package com.paperly.service;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.paperly.entity.GeneratedPaper;
import com.paperly.entity.GeneratedPaperQuestion;

@Service
public class PaperPdfService {
    public byte[] generatePdf(GeneratedPaper paper) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            Paragraph title = new Paragraph(paper.getTitle())
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(12);
            document.add(title);

            document.add(new Paragraph("Subject: " + paper.getSubject().getName())
                .setFontSize(12)
                .setMarginBottom(8));
            document.add(new Paragraph("Total Marks: " + paper.getTotalMarks())
                .setFontSize(12)
                .setMarginBottom(12));

            Table table = new Table(3);
            table.addCell(new Paragraph("No.").setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addCell(new Paragraph("Topic").setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addCell(new Paragraph("Marks").setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY));

            int index = 1;
            for (GeneratedPaperQuestion question : paper.getQuestions()) {
                table.addCell(String.valueOf(index++));
                table.addCell(question.getTopic());
                table.addCell(String.valueOf(question.getMarks()));
            }

            document.add(table.setMarginTop(12));

            int questionIndex = 1;
            for (GeneratedPaperQuestion question : paper.getQuestions()) {
                document.add(new Paragraph(questionIndex + ". " + question.getQuestionText())
                    .setMarginTop(10)
                    .setMarginBottom(6));
                document.add(new Paragraph("Topic: " + question.getTopic() + "   Difficulty: " + question.getDifficultyLevel()
                    + "   Marks: " + question.getMarks()));
                questionIndex++;
            }

            document.close();
            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Could not generate PDF", ex);
        }
    }
}
