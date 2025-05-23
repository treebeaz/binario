package com.binario.entity;

import com.binario.converter.MapJsonConverter;
import jakarta.persistence.*;
import jakarta.websocket.ClientEndpoint;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name="user_test_answer", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "test_id"})
})
public class UserTestAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private SectionsTests tests;

    @Column(name = "answer_data", columnDefinition = "jsonb")
    @Convert(converter = MapJsonConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> answerData;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "code_result", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> codeResult;

    @Column(name = "submit_at")
    private LocalDate submitAt = LocalDate.now();

    @Column(name = "teacher_comment", columnDefinition = "TEXT")
    private String teacherComment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SectionsTests getTests() {
        return tests;
    }

    public void setTests(SectionsTests tests) {
        this.tests = tests;
    }

    public Map<String, Object> getAnswerData() {
        return answerData;
    }

    public void setAnswerData(Map<String, Object> answerData) {
        this.answerData = answerData;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Map<String, Object> getCodeResult() {
        return codeResult;
    }

    public void setCodeResult(Map<String, Object> codeResult) {
        this.codeResult = codeResult;
    }

    public LocalDate getSubmitAt() {
        return submitAt;
    }

    public void setSubmitAt(LocalDate submitAt) {
        this.submitAt = submitAt;
    }

    public String getTeacherComment() {
        return teacherComment;
    }

    public void setTeacherComment(String teacherComment) {
        this.teacherComment = teacherComment;
    }
}
