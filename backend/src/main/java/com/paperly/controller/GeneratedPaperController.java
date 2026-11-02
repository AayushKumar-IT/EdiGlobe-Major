package com.paperly.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import com.paperly.dto.GeneratePaperRequest;
import com.paperly.dto.GeneratedPaperResponse;
import com.paperly.entity.GeneratedPaper;
import com.paperly.exception.ApiException;
import com.paperly.repository.GeneratedPaperRepository;
import com.paperly.service.PaperPdfService;
import com.paperly.service.PaperWordService;
import com.paperly.service.QuestionPaperGeneratorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class GeneratedPaperController {
    private final QuestionPaperGeneratorService questionPaperGeneratorService;
    private final GeneratedPaperRepository generatedPaperRepository;
    private final PaperPdfService paperPdfService;
    private final PaperWordService paperWordService;

    public GeneratedPaperController(QuestionPaperGeneratorService questionPaperGeneratorService,
                                   GeneratedPaperRepository generatedPaperRepository,
                                   PaperPdfService paperPdfService,
                                   PaperWordService paperWordService) {
        this.questionPaperGeneratorService = questionPaperGeneratorService;
        this.generatedPaperRepository = generatedPaperRepository;
        this.paperPdfService = paperPdfService;
        this.paperWordService = paperWordService;
    }

    @PostMapping("/papers/generate")
    public ResponseEntity<GeneratedPaperResponse> generatePaper(@Valid @RequestBody GeneratePaperRequest request,
                                                               Principal principal) {
        return ResponseEntity.ok(questionPaperGeneratorService.generatePaper(request, principal.getName()));
    }

    @GetMapping("/papers")
    public List<GeneratedPaperResponse> listPapers(Principal principal) {
        return questionPaperGeneratorService.getPapersForUser(principal.getName());
    }

    @GetMapping("/papers/{id}/pdf")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id, Principal principal) {
        GeneratedPaper paper = findOwnedPaper(id, principal);

        byte[] pdf = paperPdfService.generatePdf(paper);
        String filename = paper.getTitle().replaceAll("[^a-zA-Z0-9-_ ]", "").trim().replaceAll("\\s+", "-") + ".pdf";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    @GetMapping("/papers/{id}/docx")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadWord(@PathVariable Long id, Principal principal) throws java.io.IOException {
        GeneratedPaper paper = findOwnedPaper(id, principal);
        byte[] document = paperWordService.generateDocument(paper);
        String filename = safeFilename(paper.getTitle()) + ".docx";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
            .body(document);
    }

    private GeneratedPaper findOwnedPaper(Long id, Principal principal) {
        GeneratedPaper paper = generatedPaperRepository.findById(id)
            .orElseThrow(() -> new ApiException("Generated paper not found with id: " + id));
        if (!paper.getGeneratedBy().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Generated paper not found with id: " + id);
        }
        return paper;
    }

    private String safeFilename(String title) {
        String filename = title.replaceAll("[^a-zA-Z0-9-_ ]", "").trim().replaceAll("\\s+", "-");
        return filename.isEmpty() ? "question-paper" : filename;
    }
}
