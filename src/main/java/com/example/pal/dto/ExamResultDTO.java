package com.example.pal.dto;

import lombok.Data;

@Data
public class ExamResultDTO {
    private Long attemptId;
    private String examTitle;
    private Double score;
    private Double maxScore;
    private Double percentageScore;
    private String status;
    private String feedback;
    private java.time.LocalDateTime startTime;
    private java.time.LocalDateTime submissionTime;
    private java.util.List<QuestionResultDTO> questionResults;
}
