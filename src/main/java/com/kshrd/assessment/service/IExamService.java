package com.kshrd.assessment.service;

import com.kshrd.assessment.dto.exam.ExamRequest;
import com.kshrd.assessment.dto.exam.ExamResponse;
import com.kshrd.assessment.dto.exam.ExamScheduleRequest;
import com.kshrd.assessment.dto.exam.ExamScheduleResponse;
import com.kshrd.assessment.dto.exam.QuestionResponse;
import com.kshrd.assessment.dto.exam.QuestionUpdateRequest;
import com.kshrd.assessment.dto.exam.SectionResponse;
import com.kshrd.assessment.dto.exam.SectionUpdateRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IExamService {
    ExamResponse createExam(ExamRequest request);
    Optional<ExamResponse> getExamById(UUID examId);
    List<ExamResponse>  getMyExams();
    List<ExamResponse> getAllExams();
    List<ExamResponse> getActiveExams();
    Optional<ExamScheduleResponse> getExamSchedule(UUID examId);
    ExamResponse updateExam(UUID examId, ExamRequest request);
    ExamScheduleResponse updateSchedule(UUID examId, ExamScheduleRequest request);
    ExamResponse publishExam(UUID examId);
    ExamResponse unpublishExam(UUID examId);
    ExamResponse cloneExam(UUID examId);
    void deleteExam(UUID examId);
    void updateSection(UUID sectionId, SectionUpdateRequest request);
    void deleteSection(UUID sectionId);
    void updateQuestion(UUID questionId, QuestionUpdateRequest request);
    void deleteQuestion(UUID questionId);
    Optional<SectionResponse> getSectionById(UUID sectionId);
    Optional<QuestionResponse> getQuestionById(UUID questionId);
}
