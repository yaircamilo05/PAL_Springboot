package com.example.pal.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateExamDTO {
    private String title;
    private String description;
    private Long courseId;
    private Integer timeLimit; // en minutos
    private Double totalPoints;
    private Boolean active = true;
    private List<QuestionDTO> questions;
}
