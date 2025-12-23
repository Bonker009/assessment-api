package com.kshrd.assessment.repository;

import com.kshrd.assessment.entity.ClassroomAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassroomAssessmentRepository extends JpaRepository<ClassroomAssessment, UUID> {
    
    List<ClassroomAssessment> findByClassroomId(UUID classroomId);
    
    List<ClassroomAssessment> findByAssessmentId(UUID assessmentId);
    
    Optional<ClassroomAssessment> findByAssessmentIdAndClassroomId(UUID assessmentId, UUID classroomId);
    
    boolean existsByAssessmentIdAndClassroomId(UUID assessmentId, UUID classroomId);
    
    void deleteByAssessmentIdAndClassroomId(UUID assessmentId, UUID classroomId);
    
    @Query("SELECT ca.assessmentId FROM ClassroomAssessment ca WHERE ca.classroomId = :classroomId")
    List<UUID> findAssessmentIdsByClassroomId(@Param("classroomId") UUID classroomId);
    
    @Query("SELECT ca.classroomId FROM ClassroomAssessment ca WHERE ca.assessmentId = :assessmentId")
    List<UUID> findClassroomIdsByAssessmentId(@Param("assessmentId") UUID assessmentId);
}

