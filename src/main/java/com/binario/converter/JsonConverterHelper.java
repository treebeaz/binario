package com.binario.converter;

import com.binario.model.ChoiceAnswer;
import com.binario.model.TestCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class JsonConverterHelper {
    private final AnswerOptionListConverter answerOptionListConverter;
    private final ChoiceAnswerListConverter choiceAnswerListConverter;
    private final TestCaseListConverter testCaseListConverter;

    @Autowired
    public JsonConverterHelper(AnswerOptionListConverter answerOptionListConverter,
                               ChoiceAnswerListConverter choiceAnswerListConverter,
                               TestCaseListConverter testCaseListConverter) {
        this.answerOptionListConverter = answerOptionListConverter;
        this.choiceAnswerListConverter = choiceAnswerListConverter;
        this.testCaseListConverter = testCaseListConverter;
    }

    public Map<String, String> parseAnswerOptions(String json) {
        return answerOptionListConverter.convertToEntityAttribute(json);
    }

    public List<ChoiceAnswer> parseChoiceAnswers(String json) {
        return choiceAnswerListConverter.convertToEntityAttribute(json);
    }

    public List<TestCase> parseTestCases(String json) {
        return testCaseListConverter.convertToEntityAttribute(json);
    };

    public String toJsonAnswerOptions(Map<String, String> answerOptions) {
        return answerOptionListConverter.convertToDatabaseColumn(answerOptions);
    }

    public String toJsonChoiceAnswers(List<ChoiceAnswer> choiceAnswers) {
        return choiceAnswerListConverter.convertToDatabaseColumn(choiceAnswers);
    }

    public String toJsonTestCases(List<TestCase> testCases) {
        return testCaseListConverter.convertToDatabaseColumn(testCases);
    }
}
