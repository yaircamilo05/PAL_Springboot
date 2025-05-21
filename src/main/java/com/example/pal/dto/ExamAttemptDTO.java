package com.example.pal.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ExamAttemptDTO {
    private long id;
    private Long examId;
    private String examTitle;
    private LocalDateTime startTime;
    private Integer timeLimit;
    private int totalQuestions;
    private List<QuestionDTO> questions;
}
