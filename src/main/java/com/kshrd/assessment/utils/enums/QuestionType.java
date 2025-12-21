package com.kshrd.assessment.utils.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum QuestionType {
    MCQ("Multiple Choice"),
    TRUE_FALSE("True/False"),
    LONG_ANSWER("Long Answer"),
    CODING("Code Problem");

    private final String displayName;

    QuestionType(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static QuestionType fromString(String value) {
        if (value == null) {
            return null;
        }
        
        try {
            return QuestionType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            for (QuestionType type : QuestionType.values()) {
                if (type.displayName.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown QuestionType: " + value);
        }
    }
}
