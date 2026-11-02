package com.paperly.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "generated_paper_questions")
public class GeneratedPaperQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_paper_id", nullable = false)
    private GeneratedPaper generatedPaper;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(nullable = false)
    private String topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficultyLevel;

    @Column(nullable = false)
    private Integer marks;

    @Column(nullable = false)
    private Integer displayOrder;

    public GeneratedPaperQuestion() {
    }

    public GeneratedPaperQuestion(GeneratedPaper generatedPaper, String questionText, String topic,
                                 DifficultyLevel difficultyLevel, Integer marks, Integer displayOrder) {
        this.generatedPaper = generatedPaper;
        this.questionText = questionText;
        this.topic = topic;
        this.difficultyLevel = difficultyLevel;
        this.marks = marks;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GeneratedPaper getGeneratedPaper() {
        return generatedPaper;
    }

    public void setGeneratedPaper(GeneratedPaper generatedPaper) {
        this.generatedPaper = generatedPaper;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public Integer getMarks() {
        return marks;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
