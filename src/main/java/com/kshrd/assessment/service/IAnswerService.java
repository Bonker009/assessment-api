package com.kshrd.assessment.service;

import com.kshrd.assessment.dto.answer.AnswerRequest;
import com.kshrd.assessment.dto.answer.AnswerResponse;

import java.util.List;
import java.util.UUID;

import com.kshrd.assessment.dto.response.PageRequest;
import com.kshrd.assessment.dto.response.PageResponse;

public interface IAnswerService {
    AnswerResponse createAnswer(AnswerRequest request);
    AnswerResponse updateAnswer(UUID answerId, AnswerRequest request);
    AnswerResponse saveOrUpdateAnswer(AnswerRequest request);
    List<AnswerResponse> submitAllAnswers(com.kshrd.assessment.dto.answer.SubmitAllAnswersRequest request);
    AnswerResponse submitSingleAnswer(com.kshrd.assessment.dto.answer.SubmitSingleAnswerRequest request);
    List<AnswerResponse> getAnswersByAssessment(UUID assessmentId);
    PageResponse<AnswerResponse> getAnswersByAssessment(UUID assessmentId, PageRequest pageRequest);
    List<AnswerResponse> getMyAnswersByAssessment(UUID assessmentId);
    PageResponse<AnswerResponse> getMyAnswersByAssessment(UUID assessmentId, PageRequest pageRequest);
    AnswerResponse getAnswerByQuestion(UUID questionId);
    boolean isAnswerOwner(UUID answerId);
}
