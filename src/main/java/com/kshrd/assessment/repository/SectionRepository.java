package com.kshrd.assessment.repository;

import com.kshrd.assessment.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SectionRepository extends JpaRepository<Section, UUID> {
    @Query("SELECT COUNT(s) FROM Section s WHERE s.assessment.assessment_id = :assessmentId")
    Long countByAssessment_Assessment_id(@Param("assessmentId") UUID assessmentId);
    
    @Query("SELECT DISTINCT s FROM Section s LEFT JOIN FETCH s.questions WHERE s.assessment.assessment_id = :assessmentId")
    List<Section> findByAssessmentIdWithQuestions(UUID assessmentId);
}
