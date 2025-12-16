package com.kshrd.assessment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StudentAssessmentId implements Serializable {
    
    @Column(name = "student_id", nullable = false)
    private UUID studentId;
    
    @Column(name = "assessment_id", nullable = false)
    private UUID assessmentId;
}
