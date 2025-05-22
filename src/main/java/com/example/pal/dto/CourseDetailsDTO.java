package com.example.pal.dto;

import lombok.Data;
import java.util.List;

@Data
public class CourseDetailsDTO {
    private String title;
    private String description;
    private CategoryDTO category;       
    private UserResponseDTO instructor; 
    private List<ExamGetBasic> exams;
}
