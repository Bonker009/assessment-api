package com.kshrd.assessment.mapper;

import com.kshrd.assessment.dto.exam.ExamResponse;
import com.kshrd.assessment.dto.exam.ExamScheduleResponse;
import com.kshrd.assessment.dto.exam.QuestionResponse;
import com.kshrd.assessment.dto.exam.SectionResponse;
import com.kshrd.assessment.entity.Assessment;
import com.kshrd.assessment.entity.Question;
import com.kshrd.assessment.entity.Section;

public interface IExamMapper {
    ExamResponse toResponse(Assessment entity);
    ExamScheduleResponse toScheduleResponse(Assessment entity);
    SectionResponse toSectionResponse(Section entity);
    QuestionResponse toQuestionResponse(Question entity);
}
