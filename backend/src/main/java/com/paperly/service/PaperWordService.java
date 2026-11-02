package com.paperly.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import com.paperly.entity.GeneratedPaper;
import com.paperly.entity.GeneratedPaperQuestion;

@Service
public class PaperWordService {
    public byte[] generateDocument(GeneratedPaper paper) throws IOException {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText(paper.getTitle());
            titleRun.setBold(true);
            titleRun.setFontSize(18);

            addParagraph(document, "Subject: " + paper.getSubject().getName(), true);
            addParagraph(document, "Total marks: " + paper.getTotalMarks(), true);
            addParagraph(document, "", false);

            int index = 1;
            for (GeneratedPaperQuestion question : paper.getQuestions()) {
                addParagraph(document, index + ". " + question.getQuestionText()
                    + " (" + question.getMarks() + " marks)", false);
                addParagraph(document, "Topic: " + question.getTopic()
                    + " | Difficulty: " + question.getDifficultyLevel(), true);
                addParagraph(document, "", false);
                index++;
            }

            document.write(output);
            return output.toByteArray();
        }
    }

    private void addParagraph(XWPFDocument document, String text, boolean secondary) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        if (secondary) {
            run.setFontSize(9);
            run.setColor("666666");
        }
    }
}
