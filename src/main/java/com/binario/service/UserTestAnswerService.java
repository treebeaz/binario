package com.binario.service;

import com.binario.entity.SectionsTests;
import com.binario.entity.User;
import com.binario.entity.UserTestAnswer;
import com.binario.repository.UserTestAnswerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final UserTestAnswerRepository userTestAnswerRepository;

    public UserTestAnswerService(UserTestAnswerRepository userTestAnswerRepository) {
        this.userTestAnswerRepository = userTestAnswerRepository;
    }

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

        switch (questionType) {
            case "single_choice":
                return checkSingleChoice(test, answerData);
            case "multiple_choice":
                return checkMultipleChoice(test, answerData);
            case "text_answer":
                return checkTextAnswer(test, answerData);
            case "code_answer":
                return checkCodeAnswer(test, answerData, userTestAnswer);
            default:
                throw new IllegalArgumentException("Unknown question type: " + questionType);
        }
    }

    private boolean checkSingleChoice(SectionsTests test, Object answerData) {
        try {
            // Получаем правильный ответ из textAnswer
            String correctAnswer = test.getTextAnswer();
            
            // Получаем ответ пользователя из answerData
            String userAnswer = ((Map<String, Object>) answerData).get("value").toString();
            
            // Сравниваем ответы
            return correctAnswer != null && correctAnswer.trim().equals(userAnswer.trim());
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkMultipleChoice(SectionsTests test, Object answerData) {
        try {
            // Получаем правильные ответы из textAnswer
            List<String> correctAnswers;
            try {
                // Пробуем распарсить как JSON массив
                Object parsed = new ObjectMapper().readValue(test.getTextAnswer(), Object.class);
                if (parsed instanceof List) {
                    List<?> parsedList = (List<?>) parsed;
                    if (!parsedList.isEmpty() && parsedList.get(0) instanceof List) {
                        // Если это вложенный список, берем первый элемент
                        correctAnswers = ((List<?>) parsedList.get(0)).stream()
                                .map(Object::toString)
                                .collect(Collectors.toList());
                    } else {
                        // Если это простой список
                        correctAnswers = parsedList.stream()
                                .map(Object::toString)
                                .collect(Collectors.toList());
                    }
                } else {
                    // Если это не список, пробуем через запятую
                    correctAnswers = Arrays.asList(test.getTextAnswer().split(","));
                }
            } catch (Exception e) {
                // Если не получилось распарсить как JSON, пробуем через запятую
                correctAnswers = Arrays.asList(test.getTextAnswer().split(","));
            }
            System.out.println("Correct answers: " + correctAnswers);
            
            // Получаем ответы пользователя из answerData
            Map<String, Object> answerMap = (Map<String, Object>) answerData;
            System.out.println("Answer data map: " + answerMap);
            
            List<String> userAnswers;
            if (answerMap.get("values") != null) {
                userAnswers = (List<String>) answerMap.get("values");
            } else {
                userAnswers = Arrays.asList(answerMap.get("value").toString());
            }
            System.out.println("User answers: " + userAnswers);
            
            // Сортируем и сравниваем списки
            boolean result = new HashSet<>(correctAnswers).equals(new HashSet<>(userAnswers));
            System.out.println("Comparison result: " + result);
            return result;
        } catch (Exception e) {
            System.out.println("Error in checkMultipleChoice: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkTextAnswer(SectionsTests test, Object answerData) {
        try {
            // Получаем правильный ответ из textAnswer
            String correctAnswer = test.getTextAnswer();
            
            // Получаем ответ пользователя из answerData
            String userAnswer = ((Map<String, Object>) answerData).get("value").toString();
            
            // Сравниваем ответы, игнорируя регистр и пробелы
            return correctAnswer != null && 
                   correctAnswer.trim().equalsIgnoreCase(userAnswer.trim());
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkCodeAnswer(SectionsTests test, Object answerData, UserTestAnswer userTestAnswer) {
        try {
            // Получаем код пользователя из answerData
            String userCode = answerData.toString();
            
            // Создаем карту для хранения результатов выполнения
            Map<String, Object> executionResults = new HashMap<>();
            
            // TODO: Здесь должна быть реальная логика выполнения кода
            // Пока просто сохраняем код и тестовые случаи
            executionResults.put("userCode", userCode);
            executionResults.put("testCases", test.getTestCases());
            executionResults.put("executionTime", System.currentTimeMillis());
            
            // Сохраняем результаты в codeResult
            userTestAnswer.setCodeResult(executionResults);
            
            // TODO: Здесь должна быть реальная проверка результатов
            return true;
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", e.getMessage());
            userTestAnswer.setCodeResult(errorResult);
            return false;
        }
    }

    @Transactional
    public void saveAnswer(UserTestAnswer userTestAnswer) {
        // Проверяем, существует ли уже ответ для этого пользователя и теста
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
            answerToSave.setCorrect(userTestAnswer.isCorrect());
            answerToSave.setScore(userTestAnswer.getScore());
            answerToSave.setSubmitAt(userTestAnswer.getSubmitAt());
        } else {
            // Если ответа нет, создаем новый
            answerToSave = new UserTestAnswer();
            answerToSave.setUser(userTestAnswer.getUser());
            answerToSave.setTests(userTestAnswer.getTests());
            answerToSave.setAnswerData(userTestAnswer.getAnswerData());
            answerToSave.setCodeResult(userTestAnswer.getCodeResult());
            answerToSave.setCorrect(userTestAnswer.isCorrect());
            answerToSave.setScore(userTestAnswer.getScore());
            answerToSave.setSubmitAt(userTestAnswer.getSubmitAt());
        }

        // Проверяем ответ и обновляем codeResult если это тест с кодом
        if ("code_answer".equals(answerToSave.getTests().getQuestionType())) {
            checkAnswer(answerToSave.getTests(), answerToSave.getAnswerData(), answerToSave);
        }
        
        userTestAnswerRepository.save(answerToSave);
    }

    @Transactional(readOnly = true)
    public Optional<UserTestAnswer> findByUserIdAndTests(Long userId, SectionsTests test) {
        return userTestAnswerRepository.findByUserIdAndTests(userId, test);
    }
}
