package com.binario.converter;

import com.binario.model.TestCase;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Converter(autoApply = true)
public class TestCaseListConverter implements AttributeConverter<List<TestCase>, String> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<TestCase> attribute) {

        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации TestCase", e);
        }
    }

    @Override
    public List<TestCase> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return mapper.readValue(dbData, new TypeReference<List<TestCase>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Ошибка десериализации TestCase", e);
        }
    }
}
