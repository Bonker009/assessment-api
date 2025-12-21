package com.kshrd.assessment.service;

import com.kshrd.assessment.dto.answer.AnswerRequest;
import com.kshrd.assessment.dto.answer.AnswerResponse;

import java.util.List;
import java.util.UUID;

public interface IAnswerService {
    AnswerResponse createAnswer(AnswerRequest request);
    AnswerResponse updateAnswer(UUID answerId, AnswerRequest request);
    AnswerResponse saveOrUpdateAnswer(AnswerRequest request);
    List<AnswerResponse> getAnswersByAssessment(UUID assessmentId);
    List<AnswerResponse> getMyAnswersByAssessment(UUID assessmentId);
    AnswerResponse getAnswerByQuestion(UUID questionId);
    boolean isAnswerOwner(UUID answerId);
}
