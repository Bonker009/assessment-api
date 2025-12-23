package com.kshrd.assessment.repository;

import com.kshrd.assessment.entity.StudentAssessment;
import com.kshrd.assessment.utils.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentAssessmentRepository extends JpaRepository<StudentAssessment, UUID> {
    
    List<StudentAssessment> findByStudentId(UUID studentId);
    
    Page<StudentAssessment> findByStudentId(UUID studentId, Pageable pageable);
    
    List<StudentAssessment> findByAssessmentId(UUID assessmentId);
    
    Optional<StudentAssessment> findByStudentIdAndAssessmentId(UUID studentId, UUID assessmentId);
    
    List<StudentAssessment> findByStatus(Status status);
    
    List<StudentAssessment> findByStudentIdAndStatus(UUID studentId, Status status);
    
    long countByStudentIdAndStatus(UUID studentId, Status status);
    
    @Query("SELECT sa FROM StudentAssessment sa WHERE sa.gradingStatus = :gradingStatus")
    List<StudentAssessment> findByGradingStatus(@Param("gradingStatus") String gradingStatus);
    
    boolean existsByStudentIdAndAssessmentId(UUID studentId, UUID assessmentId);
    
    @Query("SELECT sa FROM StudentAssessment sa WHERE sa.status = 'IN_PROGRESS' AND sa.endsAt <= :now")
    List<StudentAssessment> findInProgressWithEndsAtBefore(@Param("now") OffsetDateTime now);
}
