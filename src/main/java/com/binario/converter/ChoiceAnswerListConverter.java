package com.binario.converter;

import com.binario.model.ChoiceAnswer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@Converter(autoApply = true)
public class ChoiceAnswerListConverter implements AttributeConverter<List<ChoiceAnswer>, String> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<ChoiceAnswer> attribute) {
        if (attribute == null) return "[]";
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации ChoiceAnswer", e);
        }
    }

    @Override
    public List<ChoiceAnswer> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) return Collections.emptyList();
        try {
            return mapper.readValue(dbData, new TypeReference<List<ChoiceAnswer>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Ошибка десериализации ChoiceAnswer", e);
        }
    }
}
