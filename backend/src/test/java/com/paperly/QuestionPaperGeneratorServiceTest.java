package com.paperly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.paperly.dto.GeneratePaperRequest;
import com.paperly.dto.GeneratedPaperResponse;
import com.paperly.dto.TopicAllocationRequest;
import com.paperly.entity.DifficultyLevel;
import com.paperly.entity.Question;
import com.paperly.entity.Subject;
import com.paperly.entity.User;
import com.paperly.entity.UserRole;
import com.paperly.exception.ApiException;
import com.paperly.repository.QuestionRepository;
import com.paperly.repository.SubjectRepository;
import com.paperly.repository.UserRepository;
import com.paperly.repository.GeneratedPaperRepository;
import com.paperly.service.PaperWordService;
import com.paperly.service.QuestionPaperGeneratorService;

@SpringBootTest
public class QuestionPaperGeneratorServiceTest {
    @Autowired
    private QuestionPaperGeneratorService generatorService;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeneratedPaperRepository generatedPaperRepository;

    @Autowired
    private PaperWordService paperWordService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        questionRepository.deleteAll();
        questionRepository.flush();
        generatedPaperRepository.deleteAll();
        generatedPaperRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
        subjectRepository.deleteAll();
        subjectRepository.flush();

        User user = userRepository.save(new User("teacher", passwordEncoder.encode("password123"), UserRole.FACULTY));
        Subject subject = subjectRepository.save(new Subject("Biology", "Life sciences"));

        questionRepository.saveAll(List.of(
            new Question("Explain photosynthesis", subject, "Plants", DifficultyLevel.EASY, 5),
            new Question("Outline the Krebs cycle", subject, "Biochemistry", DifficultyLevel.MEDIUM, 8),
            new Question("Differentiate mitosis and meiosis", subject, "Genetics", DifficultyLevel.MEDIUM, 7),
            new Question("Describe the process of digestion", subject, "Human Body", DifficultyLevel.EASY, 5),
            new Question("Explain cell respiration", subject, "Biochemistry", DifficultyLevel.HARD, 10)
        ));
    }

    @Test
    void shouldGeneratePaperWhenExactMarksMatch() {
        GeneratePaperRequest request = new GeneratePaperRequest(
            "Midterm Biology",
            subjectRepository.findAll().get(0).getId(),
            20,
            "ANY",
            List.of(
                new TopicAllocationRequest("Biochemistry", 8),
                new TopicAllocationRequest("Genetics", 7),
                new TopicAllocationRequest("Plants", 5)
            )
        );

        GeneratedPaperResponse response = generatorService.generatePaper(request, "teacher");

        assertEquals(20, response.totalMarks());
        assertEquals("Midterm Biology", response.title());
    }

    @Test
    void shouldRejectInsufficientInventoryForRequestedMarks() {
        GeneratePaperRequest request = new GeneratePaperRequest(
            "Impossible Paper",
            subjectRepository.findAll().get(0).getId(),
            30,
            "ANY",
            List.of(new TopicAllocationRequest("Genetics", 30))
        );

        assertThrows(ApiException.class, () -> generatorService.generatePaper(request, "teacher"));
    }

    @Test
    void shouldRejectDuplicateTopicAllocations() {
        GeneratePaperRequest request = new GeneratePaperRequest(
            "Duplicate topics",
            subjectRepository.findAll().get(0).getId(),
            10,
            "ANY",
            List.of(
                new TopicAllocationRequest("Plants", 5),
                new TopicAllocationRequest("plants", 5)
            )
        );

        assertThrows(ApiException.class, () -> generatorService.generatePaper(request, "teacher"));
    }

    @Test
    @Transactional
    void shouldExportGeneratedPaperAsWordDocument() throws Exception {
        GeneratePaperRequest request = new GeneratePaperRequest(
            "Word export",
            subjectRepository.findAll().get(0).getId(),
            20,
            "ANY",
            List.of(
                new TopicAllocationRequest("Biochemistry", 8),
                new TopicAllocationRequest("Genetics", 7),
                new TopicAllocationRequest("Plants", 5)
            )
        );
        GeneratedPaperResponse generatedPaper = generatorService.generatePaper(request, "teacher");

        byte[] document = paperWordService.generateDocument(
            generatedPaperRepository.findById(generatedPaper.id()).orElseThrow());

        assertTrue(document.length > 100);
        assertEquals((byte) 'P', document[0]);
        assertEquals((byte) 'K', document[1]);
    }
}
