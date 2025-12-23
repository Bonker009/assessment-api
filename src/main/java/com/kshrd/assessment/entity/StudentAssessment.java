package com.kshrd.assessment.entity;

import com.kshrd.assessment.utils.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "student_assessment")
@Getter
@Setter
public class StudentAssessment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "attempt_id", nullable = false)
    private UUID attemptId;
    
    @Column(name = "student_id", nullable = false)
    private UUID studentId;
    
    @Column(name = "assessment_id", nullable = false)
    private UUID assessmentId;
    
    @Column(name = "attempt_no", nullable = false)
    private Integer attemptNo = 1;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "assessment_id", 
        nullable = false, 
        insertable = false, 
        updatable = false
    )
    private Assessment assessment;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private Status status = Status.NOT_STARTED;
    
    @Column(name = "join_at")
    private LocalDateTime joinAt;
    
    @Column(name = "started_at")
    private OffsetDateTime startedAt;
    
    @Column(name = "ends_at")
    private OffsetDateTime endsAt;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    @Column(name = "submit_trigger", length = 10)
    private String submitTrigger;
    
    @Column(name = "duration_in_minute")
    private Double durationInMinute = 0.0;
    
    @Column(name = "total_score")
    private Double totalScore = 0.0;
    
    @Column(name = "score")
    private Double score = 0.0;
    
    @Column(name = "grading_status", length = 50)
    private String gradingStatus = "not graded";
}
