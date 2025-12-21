package com.kshrd.assessment.entity;

import com.kshrd.assessment.utils.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_assessment")
@Getter
@Setter
@IdClass(StudentAssessmentId.class)
public class StudentAssessment {
    
    @Id
    @Column(name = "student_id", nullable = false)
    private java.util.UUID studentId;
    
    @Id
    @Column(name = "assessment_id", nullable = false)
    private java.util.UUID assessmentId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "assessment_id", 
        nullable = false, 
        insertable = false, 
        updatable = false
    )
    private Assessment assessment;
    
    @Column(name = "score")
    private Double score = 0.0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private Status status = Status.ASSIGNED;
    
    @Column(name = "join_at")
    private LocalDateTime joinAt;
    
    @Column(name = "duration_in_minute")
    private Double durationInMinute = 0.0;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    @Column(name = "grading_status", length = 50)
    private String gradingStatus = "not graded";
    
    @Column(name = "total_score")
    private Double totalScore = 0.0;
}
