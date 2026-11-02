package com.paperly.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paperly.dto.GeneratePaperRequest;
import com.paperly.dto.GeneratedPaperQuestionResponse;
import com.paperly.dto.GeneratedPaperResponse;
import com.paperly.dto.TopicAllocationRequest;
import com.paperly.entity.GeneratedPaper;
import com.paperly.entity.GeneratedPaperQuestion;
import com.paperly.entity.Question;
import com.paperly.entity.Subject;
import com.paperly.entity.User;
import com.paperly.exception.ApiException;
import com.paperly.repository.GeneratedPaperRepository;
import com.paperly.repository.QuestionRepository;
import com.paperly.repository.SubjectRepository;
import com.paperly.repository.UserRepository;

@Service
public class QuestionPaperGeneratorService {
    private final SubjectRepository subjectRepository;
    private final QuestionRepository questionRepository;
    private final GeneratedPaperRepository generatedPaperRepository;
    private final UserRepository userRepository;

    public QuestionPaperGeneratorService(SubjectRepository subjectRepository,
                                         QuestionRepository questionRepository,
                                         GeneratedPaperRepository generatedPaperRepository,
                                         UserRepository userRepository) {
        this.subjectRepository = subjectRepository;
        this.questionRepository = questionRepository;
        this.generatedPaperRepository = generatedPaperRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public GeneratedPaperResponse generatePaper(GeneratePaperRequest request, String username) {
        if (request.totalMarks() == null || request.totalMarks() < 1 || request.totalMarks() > 1000) {
            throw new ApiException("Total marks must be between 1 and 1000.");
        }
        validateDifficulty(request.difficultyLevel());

        Subject subject = subjectRepository.findById(request.subjectId())
            .orElseThrow(() -> new ApiException("Subject not found with id: " + request.subjectId()));

        User generatedBy = userRepository.findByUsername(username)
            .orElseThrow(() -> new ApiException("User not found: " + username));

        List<Question> allQuestions = questionRepository.findBySubjectId(request.subjectId());
        if (allQuestions.isEmpty()) {
            throw new ApiException("No questions available for this subject. Add questions before generating a paper.");
        }

        List<TopicAllocationRequest> allocations = request.topicAllocations() == null || request.topicAllocations().isEmpty()
            ? inferTopicAllocations(allQuestions, request.totalMarks())
            : request.topicAllocations();

        int allocatedMarks = allocations.stream().mapToInt(TopicAllocationRequest::marks).sum();
        if (allocatedMarks != request.totalMarks()) {
            throw new ApiException("The marks in your topic allocation must total exactly " + request.totalMarks() + ".");
        }

        List<Question> selectedQuestions = new ArrayList<>();
        Set<Long> usedQuestionIds = new HashSet<>();
        Set<String> allocatedTopics = new HashSet<>();
        for (TopicAllocationRequest allocation : allocations) {
            if (allocation.marks() <= 0) {
                throw new ApiException("Topic allocation marks must be positive for topic: " + allocation.topic());
            }
            if (!allocatedTopics.add(allocation.topic().trim().toLowerCase())) {
                throw new ApiException("Each topic can only be allocated once: " + allocation.topic());
            }

            List<Question> candidates = allQuestions.stream()
                .filter(question -> question.getTopic().equalsIgnoreCase(allocation.topic().trim()))
                .filter(question -> matchesDifficulty(question, request.difficultyLevel()))
                .filter(question -> !usedQuestionIds.contains(question.getId()))
                .toList();

            if (candidates.isEmpty()) {
                throw new ApiException("Insufficient questions for topic '" + allocation.topic() + "' matching the selected difficulty.");
            }

            List<Question> chosen = findExactSubset(candidates, allocation.marks());
            if (chosen.isEmpty()) {
                throw new ApiException("No valid question combination found for topic '" + allocation.topic() + "' totaling " + allocation.marks() + " marks.");
            }

            for (Question question : chosen) {
                selectedQuestions.add(question);
                usedQuestionIds.add(question.getId());
            }
        }

        GeneratedPaper generatedPaper = new GeneratedPaper(request.title(), subject, generatedBy, request.totalMarks());
        int displayOrder = 1;
        for (Question question : selectedQuestions) {
            GeneratedPaperQuestion paperQuestion = new GeneratedPaperQuestion(
                generatedPaper,
                question.getQuestionText(),
                question.getTopic(),
                question.getDifficultyLevel(),
                question.getMarks(),
                displayOrder++
            );
            generatedPaper.getQuestions().add(paperQuestion);
        }

        GeneratedPaper savedPaper = generatedPaperRepository.save(generatedPaper);
        return toResponse(savedPaper);
    }

    @Transactional(readOnly = true)
    public List<GeneratedPaperResponse> getPapersForUser(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ApiException("User not found: " + username));

        return generatedPaperRepository.findByGeneratedByOrderByGeneratedDateDesc(user)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private boolean matchesDifficulty(Question question, String difficultyLevel) {
        if (difficultyLevel == null || difficultyLevel.equalsIgnoreCase("ANY")) {
            return true;
        }
        return question.getDifficultyLevel().name().equalsIgnoreCase(difficultyLevel.trim());
    }

    private void validateDifficulty(String difficultyLevel) {
        if (difficultyLevel == null || difficultyLevel.isBlank() || difficultyLevel.equalsIgnoreCase("ANY")) {
            return;
        }
        try {
            com.paperly.entity.DifficultyLevel.valueOf(difficultyLevel.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ApiException("Difficulty must be EASY, MEDIUM, HARD, or ANY.");
        }
    }

    private List<TopicAllocationRequest> inferTopicAllocations(List<Question> questions, int totalMarks) {
        List<String> availableTopics = questions.stream()
            .map(Question::getTopic)
            .distinct()
            .sorted()
            .toList();

        if (availableTopics.isEmpty()) {
            throw new ApiException("No topics available for this subject.");
        }

        List<String> topics = new ArrayList<>(availableTopics);
        Collections.shuffle(topics);
        if (topics.size() > totalMarks) {
            topics = new ArrayList<>(topics.subList(0, totalMarks));
        }
        int perTopic = totalMarks / topics.size();
        int remainder = totalMarks % topics.size();
        List<TopicAllocationRequest> allocations = new ArrayList<>();
        for (int i = 0; i < topics.size(); i++) {
            int marks = perTopic + (i < remainder ? 1 : 0);
            allocations.add(new TopicAllocationRequest(topics.get(i), marks));
        }
        return allocations;
    }

    private List<Question> findExactSubset(List<Question> candidates, int targetMarks) {
        List<Question> randomized = new ArrayList<>(candidates);
        Collections.shuffle(randomized);
        boolean[] reachable = new boolean[targetMarks + 1];
        int[] previousTotal = new int[targetMarks + 1];
        int[] selectedQuestion = new int[targetMarks + 1];
        java.util.Arrays.fill(selectedQuestion, -1);
        reachable[0] = true;

        for (int questionIndex = 0; questionIndex < randomized.size(); questionIndex++) {
            int marks = randomized.get(questionIndex).getMarks();
            if (marks > targetMarks) {
                continue;
            }
            for (int total = targetMarks; total >= marks; total--) {
                if (!reachable[total] && reachable[total - marks]) {
                    reachable[total] = true;
                    previousTotal[total] = total - marks;
                    selectedQuestion[total] = questionIndex;
                }
            }
            if (reachable[targetMarks]) {
                break;
            }
        }

        if (!reachable[targetMarks]) {
            return List.of();
        }

        List<Question> selected = new ArrayList<>();
        for (int total = targetMarks; total > 0; total = previousTotal[total]) {
            selected.add(randomized.get(selectedQuestion[total]));
        }
        Collections.reverse(selected);
        return selected;
    }

    private GeneratedPaperResponse toResponse(GeneratedPaper paper) {
        List<GeneratedPaperQuestionResponse> questions = paper.getQuestions().stream()
            .map(question -> new GeneratedPaperQuestionResponse(
                question.getId(),
                question.getQuestionText(),
                question.getTopic(),
                question.getDifficultyLevel(),
                question.getMarks(),
                question.getDisplayOrder()
            )).toList();

        return new GeneratedPaperResponse(
            paper.getId(),
            paper.getTitle(),
            paper.getSubject().getId(),
            paper.getSubject().getName(),
            paper.getGeneratedBy().getUsername(),
            paper.getGeneratedDate(),
            paper.getTotalMarks(),
            questions
        );
    }
}
