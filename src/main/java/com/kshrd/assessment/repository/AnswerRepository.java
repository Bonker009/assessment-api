package com.kshrd.assessment.repository;

import com.kshrd.assessment.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    
    @Query("SELECT a FROM Answer a WHERE a.attemptId = :attemptId")
    List<Answer> findByAttemptId(@Param("attemptId") UUID attemptId);
    
    @Query("SELECT a FROM Answer a WHERE a.attemptId = :attemptId")
    Page<Answer> findByAttemptId(@Param("attemptId") UUID attemptId, Pageable pageable);
    
    @Query("SELECT a FROM Answer a WHERE a.question.question_id = :questionId")
    List<Answer> findByQuestion_Question_id(@Param("questionId") UUID questionId);
    
    @Query("SELECT a FROM Answer a WHERE a.attemptId = :attemptId AND a.question.question_id = :questionId")
    Optional<Answer> findByAttemptIdAndQuestion_Question_id(@Param("attemptId") UUID attemptId, @Param("questionId") UUID questionId);
    
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Answer a WHERE a.attemptId = :attemptId AND a.question.question_id = :questionId")
    boolean existsByAttemptIdAndQuestion_Question_id(@Param("attemptId") UUID attemptId, @Param("questionId") UUID questionId);
    
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.attemptId = :attemptId")
    Long countByAttemptId(@Param("attemptId") UUID attemptId);
}

