package com.kshrd.assessment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "answer",
    uniqueConstraints = {
        @UniqueConstraint(name = "uniq_answer_per_attempt_question", columnNames = {"attempt_id", "question_id"})
    })
@Getter
@Setter
public class Answer {

    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "answer_id", nullable = false)
    private UUID answerId;

    @Column(name = "attempt_id", nullable = false)
    private UUID attemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "attempt_id", 
        nullable = false, 
        insertable = false, 
        updatable = false
    )
    private StudentAssessment studentAssessment;

    @Column(name = "answer", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> answer;

    @Column(name = "score")
    private Double score;

    @Column(name = "saved_at", nullable = false)
    private OffsetDateTime savedAt;

    @PrePersist
    protected void onCreate() {
        if (savedAt == null) {
            savedAt = OffsetDateTime.now(UTC_ZONE);
        }
    }
}
