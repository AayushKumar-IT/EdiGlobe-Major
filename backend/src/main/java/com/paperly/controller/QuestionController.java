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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import com.paperly.dto.QuestionRequest;
import com.paperly.dto.QuestionResponse;
import com.paperly.entity.DifficultyLevel;
import com.paperly.entity.Question;
import com.paperly.entity.Subject;
import com.paperly.exception.ApiException;
import com.paperly.repository.QuestionRepository;
import com.paperly.repository.SubjectRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class QuestionController {
    private final QuestionRepository questionRepository;
    private final SubjectRepository subjectRepository;

    public QuestionController(QuestionRepository questionRepository, SubjectRepository subjectRepository) {
        this.questionRepository = questionRepository;
        this.subjectRepository = subjectRepository;
    }

    @GetMapping("/questions")
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestions(
        @RequestParam(required = false) Long subjectId,
        @RequestParam(required = false) String difficultyLevel,
        @RequestParam(required = false) String search) {

        List<Question> questions = questionRepository.findAll();
        if (subjectId != null) {
            questions = questions.stream().filter(q -> q.getSubject().getId().equals(subjectId)).toList();
        }
        if (difficultyLevel != null && !difficultyLevel.isBlank()) {
            questions = questions.stream().filter(q -> q.getDifficultyLevel().name().equalsIgnoreCase(difficultyLevel)).toList();
        }
        if (search != null && !search.isBlank()) {
            String query = search.trim().toLowerCase();
            questions = questions.stream().filter(q ->
                q.getQuestionText().toLowerCase().contains(query)
                    || q.getTopic().toLowerCase().contains(query)
                    || q.getSubject().getName().toLowerCase().contains(query)).toList();
        }

        return questions.stream()
            .sorted((a, b) -> a.getId().compareTo(b.getId()))
            .map(this::toResponse)
            .toList();
    }

    @PostMapping("/questions")
    @Transactional
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody QuestionRequest request) {
        Subject subject = subjectRepository.findById(request.subjectId())
            .orElseThrow(() -> new ApiException("Subject not found with id: " + request.subjectId()));

        DifficultyLevel level = parseDifficulty(request.difficultyLevel());
        Question question = new Question(
            request.questionText().trim(),
            subject,
            request.topic().trim(),
            level,
            request.marks()
        );

        Question saved = questionRepository.save(question);
        return ResponseEntity.ok(toResponse(saved));
    }

    @PutMapping("/questions/{id}")
    @Transactional
    public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable Long id, @Valid @RequestBody QuestionRequest request) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new ApiException("Question not found with id: " + id));

        Subject subject = subjectRepository.findById(request.subjectId())
            .orElseThrow(() -> new ApiException("Subject not found with id: " + request.subjectId()));

        question.setQuestionText(request.questionText().trim());
        question.setSubject(subject);
        question.setTopic(request.topic().trim());
        question.setDifficultyLevel(parseDifficulty(request.difficultyLevel()));
        question.setMarks(request.marks());
        Question updated = questionRepository.save(question);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/questions/{id}")
    @Transactional
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new ApiException("Question not found with id: " + id));
        questionRepository.delete(question);
        return ResponseEntity.noContent().build();
    }

    private QuestionResponse toResponse(Question question) {
        return new QuestionResponse(
            question.getId(),
            question.getQuestionText(),
            question.getSubject().getId(),
            question.getSubject().getName(),
            question.getTopic(),
            question.getDifficultyLevel(),
            question.getMarks()
        );
    }

    private DifficultyLevel parseDifficulty(String difficultyLevel) {
        try {
            return DifficultyLevel.valueOf(difficultyLevel.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ApiException("Difficulty must be EASY, MEDIUM, or HARD.");
        }
    }
}
