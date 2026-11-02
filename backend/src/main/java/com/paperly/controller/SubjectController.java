package com.paperly.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paperly.dto.SubjectRequest;
import com.paperly.dto.SubjectResponse;
import com.paperly.entity.Subject;
import com.paperly.exception.ApiException;
import com.paperly.repository.QuestionRepository;
import com.paperly.repository.GeneratedPaperRepository;
import com.paperly.repository.SubjectRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class SubjectController {
    private final SubjectRepository subjectRepository;
    private final QuestionRepository questionRepository;
    private final GeneratedPaperRepository generatedPaperRepository;

    public SubjectController(SubjectRepository subjectRepository,
                             QuestionRepository questionRepository,
                             GeneratedPaperRepository generatedPaperRepository) {
        this.subjectRepository = subjectRepository;
        this.questionRepository = questionRepository;
        this.generatedPaperRepository = generatedPaperRepository;
    }

    @GetMapping("/subjects")
    public List<SubjectResponse> getAllSubjects() {
        return subjectRepository.findAll().stream()
            .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
            .map(subject -> new SubjectResponse(subject.getId(), subject.getName(), subject.getDescription()))
            .toList();
    }

    @PostMapping("/subjects")
    public ResponseEntity<SubjectResponse> createSubject(@Valid @RequestBody SubjectRequest request) {
        if (subjectRepository.findByNameIgnoreCase(request.name().trim()).isPresent()) {
            throw new ApiException("A subject with this name already exists.");
        }

        Subject subject = subjectRepository.save(new Subject(request.name().trim(), request.description() == null ? "" : request.description().trim()));
        return ResponseEntity.ok(new SubjectResponse(subject.getId(), subject.getName(), subject.getDescription()));
    }

    @PutMapping("/subjects/{id}")
    public ResponseEntity<SubjectResponse> updateSubject(@PathVariable Long id, @Valid @RequestBody SubjectRequest request) {
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new ApiException("Subject not found with id: " + id));

        subjectRepository.findByNameIgnoreCase(request.name().trim())
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing -> {
                throw new ApiException("A subject with this name already exists.");
            });

        subject.setName(request.name().trim());
        subject.setDescription(request.description() == null ? "" : request.description().trim());
        subjectRepository.save(subject);
        return ResponseEntity.ok(new SubjectResponse(subject.getId(), subject.getName(), subject.getDescription()));
    }

    @DeleteMapping("/subjects/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new ApiException("Subject not found with id: " + id));

        if (!questionRepository.findBySubject(subject).isEmpty()) {
            throw new ApiException("This subject contains questions and cannot be deleted until it is empty.");
        }
        if (generatedPaperRepository.existsBySubject(subject)) {
            throw new ApiException("This subject is referenced by saved papers and cannot be deleted.");
        }

        subjectRepository.delete(subject);
        return ResponseEntity.noContent().build();
    }
}
