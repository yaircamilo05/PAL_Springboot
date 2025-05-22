package com.example.pal.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class StudentProgressReportDTO {
    private String studentName;
    private Double examCompletionPercentage;
    private Double averageScore;
}
