package com.kshrd.assessment.repository;

import com.kshrd.assessment.entity.Assessment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     
     Page<Assessment> findByCreatedBy(UUID createdBy, Pageable pageable);
     
     @Query("SELECT a FROM Assessment a WHERE a.createdBy = :createdBy AND LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%'))")
     Page<Assessment> findByCreatedByAndNameContainingIgnoreCase(UUID createdBy, String search, Pageable pageable);
     
     Page<Assessment> findAll(Pageable pageable);
     
     @Query("SELECT a FROM Assessment a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%'))")
     Page<Assessment> findByNameContainingIgnoreCase(String search, Pageable pageable);

     @Query("SELECT DISTINCT a FROM Assessment a LEFT JOIN FETCH a.sections")
     List<Assessment> findAllWithSections();
     
     @Query("SELECT DISTINCT a FROM Assessment a LEFT JOIN FETCH a.sections WHERE a.assessment_id = :assessmentId")
     java.util.Optional<Assessment> findByIdWithSections(UUID assessmentId);
     
     @Query("SELECT DISTINCT a FROM Assessment a LEFT JOIN FETCH a.sections " +
            "WHERE a.assessmentDate IS NOT NULL " +
            "AND a.startTime IS NOT NULL " +
            "AND a.endTime IS NOT NULL")
     List<Assessment> findActiveExamsWithSections();
     
     @Query("SELECT a FROM Assessment a " +
            "WHERE a.assessmentDate IS NOT NULL " +
            "AND a.startTime IS NOT NULL " +
            "AND a.endTime IS NOT NULL")
     List<Assessment> findPublishedExamsWithSchedule();
     
     @Query("SELECT a FROM Assessment a " +
            "WHERE a.assessmentDate IS NOT NULL " +
            "AND a.startTime IS NOT NULL " +
            "AND a.endTime IS NOT NULL")
     Page<Assessment> findPublishedExamsWithSchedule(Pageable pageable);
     
     @Query("SELECT a FROM Assessment a " +
            "WHERE a.assessmentDate IS NOT NULL " +
            "AND a.startTime IS NOT NULL " +
            "AND a.endTime IS NOT NULL " +
            "AND LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%'))")
     Page<Assessment> findPublishedExamsWithScheduleAndNameContaining(String search, Pageable pageable);
}
