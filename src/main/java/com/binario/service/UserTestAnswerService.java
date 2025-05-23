package com.binario.service;

import com.binario.entity.SectionsTests;
import com.binario.entity.User;
import com.binario.entity.UserTestAnswer;
import com.binario.model.ChoiceAnswer;
import com.binario.repository.UserTestAnswerRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserTestAnswerService {
    @Autowired
    private UserTestAnswerRepository userTestAnswerRepository;

    public UserTestAnswer submitAnswer(UserTestAnswer userTestAnswer) {
        return userTestAnswerRepository.save(userTestAnswer);
    }

    public UserTestAnswer getAnswer(User user, SectionsTests section) {
        return userTestAnswerRepository.findByUserIdAndTests(user.getId(), section)
                .orElseThrow( () -> new RuntimeException("Answer not found"));
    }

    public List<UserTestAnswer> getAnswersByUser(Long userId) {
        return userTestAnswerRepository.findByUserId(userId);
    }

    public boolean checkAnswer(SectionsTests test, Object answerData, UserTestAnswer userTestAnswer) {
        String questionType = test.getQuestionType();

        return switch (questionType) {
            case "single_choice" -> checkSingleChoice(test, answerData);
            case "multiple_choice" -> checkMultipleChoice(test, answerData);
            case "text_answer" -> checkTextAnswer(test, answerData);
            case "code_answer" -> checkCodeAnswer(test, answerData, userTestAnswer);
            default -> throw new IllegalArgumentException("Unknown question type: " + questionType);
        };
    }

    private boolean checkSingleChoice(SectionsTests test, Object answerData) {
        try {
            List<ChoiceAnswer> correctAnswersList = test.getChoiceAnswers();

            String correctAnswer = correctAnswersList.get(0).getCorrect().get(0);

            String userAnswer = ((Map<String, Object>) answerData).get("value").toString();

            return correctAnswer != null && correctAnswer.trim().equals(userAnswer.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkMultipleChoice(SectionsTests test, Object answerData) {
        try {
            List<ChoiceAnswer> correctAnswersList = test.getChoiceAnswers();

            List<String> correctAnswers = correctAnswersList.get(0).getCorrect();

            Map<String, Object> answerMap = (Map<String, Object>) answerData;
            List<String> userAnswers;
            if (answerMap.get("values") != null) {
                userAnswers = (List<String>) answerMap.get("values");
            } else {
                userAnswers = Arrays.asList(answerMap.get("value").toString());
            }

            return new HashSet<>(correctAnswers).equals(new HashSet<>(userAnswers));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkTextAnswer(SectionsTests test, Object answerData) {
        try {
            String correctAnswer = test.getTextAnswer();
            
            String userAnswer = ((Map<String, Object>) answerData).get("value").toString();
            
            return correctAnswer != null &&
                   correctAnswer.trim().equalsIgnoreCase(userAnswer.trim());
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkCodeAnswer(SectionsTests test, Object answerData, UserTestAnswer userTestAnswer) {
        try {
            String userCode = answerData.toString();

            Map<String, Object> codeResult = new HashMap<>();
            codeResult.put("code", userCode);
            
            userTestAnswer.setCodeResult(codeResult);
            return false;
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", e.getMessage());
            userTestAnswer.setCodeResult(errorResult);
            return false;
        }
    }

    @Transactional
    public void saveAnswer(UserTestAnswer userTestAnswer) {
        Optional<UserTestAnswer> existingAnswer = userTestAnswerRepository.findByUserIdAndTests(
            userTestAnswer.getUser().getId(), 
            userTestAnswer.getTests()
        );

        UserTestAnswer answerToSave;
        if (existingAnswer.isPresent()) {
            // Если ответ существует, обновляем его
            answerToSave = existingAnswer.get();
            answerToSave.setAnswerData(userTestAnswer.getAnswerData());
            answerToSave.setCodeResult(userTestAnswer.getCodeResult());
            answerToSave.setScore(userTestAnswer.getScore());
            answerToSave.setSubmitAt(userTestAnswer.getSubmitAt());
        } else {
            // Если ответа нет, создаем новый
            answerToSave = new UserTestAnswer();
            answerToSave.setUser(userTestAnswer.getUser());
            answerToSave.setTests(userTestAnswer.getTests());
            answerToSave.setAnswerData(userTestAnswer.getAnswerData());
            answerToSave.setCodeResult(userTestAnswer.getCodeResult());
            answerToSave.setScore(userTestAnswer.getScore());
            answerToSave.setSubmitAt(userTestAnswer.getSubmitAt());
        }

        if ("code_answer".equals(answerToSave.getTests().getQuestionType())) {
            checkAnswer(answerToSave.getTests(), answerToSave.getAnswerData(), answerToSave);
        } else {
            boolean correct = checkAnswer(answerToSave.getTests(), answerToSave.getAnswerData(), answerToSave);
            answerToSave.setCorrect(correct);
        }
        
        userTestAnswerRepository.save(answerToSave);
    }

    @Transactional(readOnly = true)
    public Optional<UserTestAnswer> findByUserIdAndTests(Long userId, SectionsTests test) {
        return userTestAnswerRepository.findByUserIdAndTests(userId, test);
    }

    public List<UserTestAnswer> findUnverifiedCodeAnswers() {
        List<UserTestAnswer> answers = userTestAnswerRepository.findByCodeResultIsNotNullAndIsCorrectFalse();
        return answers;
    }
    
    public UserTestAnswer findById(Long id) {
        return userTestAnswerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Answer not found"));
    }
    
    @Transactional
    public void reviewCodeAnswer(Long answerId, Integer score, String comment) {
        UserTestAnswer answer = findById(answerId);
        answer.setTeacherComment(comment);
        answer.setScore(score);
        answer.setCorrect(true);
        userTestAnswerRepository.save(answer);
    }
}
