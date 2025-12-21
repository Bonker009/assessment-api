package com.kshrd.assessment.repository;

import com.kshrd.assessment.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    
    @Query("SELECT a FROM Answer a WHERE a.student_id = :studentId")
    List<Answer> findByStudent_id(@Param("studentId") UUID studentId);
    
    @Query("SELECT a FROM Answer a WHERE a.assessment.assessment_id = :assessmentId")
    List<Answer> findByAssessment_Assessment_id(@Param("assessmentId") UUID assessmentId);
    
    @Query("SELECT a FROM Answer a WHERE a.student_id = :studentId AND a.assessment.assessment_id = :assessmentId")
    List<Answer> findByStudent_idAndAssessment_Assessment_id(@Param("studentId") UUID studentId, @Param("assessmentId") UUID assessmentId);
    
    @Query("SELECT a FROM Answer a WHERE a.student_id = :studentId AND a.question.question_id = :questionId")
    Optional<Answer> findByStudent_idAndQuestion_Question_id(@Param("studentId") UUID studentId, @Param("questionId") UUID questionId);
    
    @Query("SELECT a FROM Answer a WHERE a.question.question_id = :questionId")
    List<Answer> findByQuestion_Question_id(@Param("questionId") UUID questionId);
    
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.student_id = :studentId AND a.assessment.assessment_id = :assessmentId")
    Long countByStudentIdAndAssessmentId(@Param("studentId") UUID studentId, @Param("assessmentId") UUID assessmentId);
    
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Answer a WHERE a.student_id = :studentId AND a.question.question_id = :questionId")
    boolean existsByStudent_idAndQuestion_Question_id(@Param("studentId") UUID studentId, @Param("questionId") UUID questionId);
}

