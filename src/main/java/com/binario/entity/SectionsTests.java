package com.binario.entity;

import com.binario.converter.AnswerOptionListConverter;
import com.binario.converter.TestCaseListConverter;
import com.binario.model.AnswerOption;
import com.binario.model.TestCase;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "sections_tests")
public class SectionsTests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private CourseSection section;

    @Column(name = "question_type", nullable = false)
    private String questionType;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "answer_options", columnDefinition = "jsonb")
    @Convert(converter = AnswerOptionListConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> answerOptions;

    @Column(name = "text_answer", columnDefinition = "TEXT")
    private String textAnswer;

    @Column(name = "code", columnDefinition = "TEXT")
    private String code;

    @Column(name = "test_case", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<TestCase> testCases;

    @Column(name = "max_score", nullable = false)
    private Integer maxScore = 1;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "tests", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTestAnswer> userTestAnswers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CourseSection getSection() {
        return section;
    }

    public void setSection(CourseSection section) {
        this.section = section;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Map<String, String> getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(Map<String, String> answerOptions) {
        this.answerOptions = answerOptions;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<UserTestAnswer> getUserTestAnswers() {
        return userTestAnswers;
    }

    public void setUserTestAnswers(List<UserTestAnswer> userTestAnswers) {
        this.userTestAnswers = userTestAnswers;
    }
}
