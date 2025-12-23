package com.kshrd.assessment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "assessment_classroom",
    uniqueConstraints = {
        @UniqueConstraint(name = "uniq_assessment_classroom", columnNames = {"assessment_id", "classroom_id"})
    })
@Getter
@Setter
public class ClassroomAssessment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;
    
    @Column(name = "assessment_id", nullable = false)
    private UUID assessmentId;
    
    @Column(name = "classroom_id", nullable = false)
    private UUID classroomId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false, insertable = false, updatable = false)
    private Assessment assessment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false, insertable = false, updatable = false)
    private Classroom classroom;
    
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;
    
    @PrePersist
    protected void onCreate() {
        if (assignedAt == null) {
            assignedAt = LocalDateTime.now();
        }
    }
}

