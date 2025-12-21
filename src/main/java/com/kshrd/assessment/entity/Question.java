package com.kshrd.assessment.entity;

import com.kshrd.assessment.utils.enums.QuestionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "questions")
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID question_id;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    private String image;

    @Column(name = "question_content", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> questionContent;

    private Double points;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    Section section;
}
