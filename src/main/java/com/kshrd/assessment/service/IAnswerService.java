package com.kshrd.assessment.service;

import com.kshrd.assessment.entity.Answer;

import java.util.UUID;

public interface IAnswerService {
    Answer createAnswer(Answer answer);
    boolean isAnswerOwner(UUID answerId);
}
