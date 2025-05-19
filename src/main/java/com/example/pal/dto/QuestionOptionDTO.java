package com.example.pal.dto;

import lombok.Data;

@Data
public class QuestionOptionDTO {
    private String text;
    private Boolean isCorrect = false;
}
