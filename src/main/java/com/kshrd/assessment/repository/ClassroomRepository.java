package com.kshrd.assessment.repository;

import com.kshrd.assessment.entity.Classroom;
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
public interface ClassroomRepository extends JpaRepository<Classroom, UUID> {
    
    List<Classroom> findByTeacherId(UUID teacherId);
    
    Page<Classroom> findByTeacherId(UUID teacherId, Pageable pageable);
    
    @Query("SELECT c FROM Classroom c WHERE c.teacherId = :teacherId AND LOWER(c.className) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Classroom> findByTeacherIdAndClassNameContainingIgnoreCase(
            @Param("teacherId") UUID teacherId,
            @Param("search") String search,
            Pageable pageable
    );
    
    @Query("SELECT c FROM Classroom c WHERE LOWER(c.className) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Classroom> findByClassNameContainingIgnoreCase(@Param("search") String search, Pageable pageable);
    
    List<Classroom> findBySubjectId(UUID subjectId);
    
    Optional<Classroom> findByClassroomIdAndTeacherId(UUID classroomId, UUID teacherId);
    
    boolean existsByClassNameAndTeacherId(String className, UUID teacherId);
}

