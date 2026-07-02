package com.example.ramenpicker.common;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * List&lt;String&gt; 태그를 콤마 구분 TEXT 컬럼으로 저장/복원하는 컨버터.
 * (PostgreSQL/H2 공통으로 TEXT 하나에 담기 위함)
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }
        return attribute.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(DELIMITER));
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return Arrays.stream(dbData.split(DELIMITER))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
