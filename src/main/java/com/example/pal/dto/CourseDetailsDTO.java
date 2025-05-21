package com.example.pal.dto;

import java.util.List;

import lombok.Data;


@Data
public class CourseDetailsDTO {
    private String title;
    private String description;
    private String category;
    private String instructor;
    private List<ExamGetBasic> exams;
}
