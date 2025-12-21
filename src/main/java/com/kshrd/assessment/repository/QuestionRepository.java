package com.kshrd.assessment.repository;

import com.kshrd.assessment.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
    @Query("SELECT COUNT(q) FROM Question q WHERE q.section.assessment.assessment_id = :assessmentId")
    Long countByAssessmentId(@Param("assessmentId") UUID assessmentId);
}
