package com.kshrd.assessment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "classrooms")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Classroom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "classroom_id", nullable = false)
    private UUID classroomId;
    
    @Column(name = "class_name", nullable = false, length = 255)
    private String className;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "subject_id")
    private UUID subjectId;
    
    @Column(name = "teacher_id", nullable = false)
    private UUID teacherId;
    
    @CreatedBy
    @Column(name = "created_by")
    private UUID createdBy;
    
    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

