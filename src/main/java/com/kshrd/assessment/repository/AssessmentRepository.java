package com.kshrd.assessment.repository;

import com.kshrd.assessment.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {
     @Query("SELECT DISTINCT a FROM Assessment a LEFT JOIN FETCH a.sections WHERE a.createdBy = :createdBy")
     List<Assessment> findByCreatedByWithSections(UUID createdBy);
     
     List<Assessment> findByCreatedBy(UUID createdBy);

     @Query("SELECT DISTINCT a FROM Assessment a LEFT JOIN FETCH a.sections")
     List<Assessment> findAllWithSections();
     
     @Query("SELECT DISTINCT a FROM Assessment a LEFT JOIN FETCH a.sections WHERE a.assessment_id = :assessmentId")
     java.util.Optional<Assessment> findByIdWithSections(UUID assessmentId);
     
     @Query("SELECT DISTINCT a FROM Assessment a LEFT JOIN FETCH a.sections " +
            "WHERE a.isPublished = true " +
            "AND a.assessmentDate IS NOT NULL " +
            "AND a.startTime IS NOT NULL " +
            "AND a.endTime IS NOT NULL")
     List<Assessment> findActiveExamsWithSections();
     
     @Query("SELECT a FROM Assessment a " +
            "WHERE a.isPublished = true " +
            "AND a.assessmentDate IS NOT NULL " +
            "AND a.startTime IS NOT NULL " +
            "AND a.endTime IS NOT NULL")
     List<Assessment> findPublishedExamsWithSchedule();
     
     @Query(value = "SELECT * FROM assessments a " +
            "WHERE a.is_published = true " +
            "AND a.assessment_date IS NOT NULL " +
            "AND a.start_time IS NOT NULL " +
            "AND a.end_time IS NOT NULL " +
            "AND a.assessment_date = CURRENT_DATE " +
            "AND CAST(CURRENT_TIME AS TIME) >= CAST(a.start_time AS TIME) " +
            "AND CAST(CURRENT_TIME AS TIME) <= CAST(a.end_time AS TIME)", 
            nativeQuery = true)
     List<Assessment> findActiveExamsNative();
}
