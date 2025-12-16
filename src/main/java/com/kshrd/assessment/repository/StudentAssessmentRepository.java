package com.kshrd.assessment.repository;

import com.kshrd.assessment.entity.StudentAssessment;
import com.kshrd.assessment.entity.StudentAssessmentId;
import com.kshrd.assessment.utils.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentAssessmentRepository extends JpaRepository<StudentAssessment, StudentAssessmentId> {
    
    List<StudentAssessment> findByStudentId(UUID studentId);
    
    List<StudentAssessment> findByAssessmentId(UUID assessmentId);
    
    Optional<StudentAssessment> findByStudentIdAndAssessmentId(UUID studentId, UUID assessmentId);
    
    List<StudentAssessment> findByStatus(Status status);
    
    List<StudentAssessment> findByStudentIdAndStatus(UUID studentId, Status status);
    
    long countByStudentIdAndStatus(UUID studentId, Status status);
    
    @Query("SELECT sa FROM StudentAssessment sa WHERE sa.gradingStatus = :gradingStatus")
    List<StudentAssessment> findByGradingStatus(@Param("gradingStatus") String gradingStatus);
    
    boolean existsByStudentIdAndAssessmentId(UUID studentId, UUID assessmentId);
}
