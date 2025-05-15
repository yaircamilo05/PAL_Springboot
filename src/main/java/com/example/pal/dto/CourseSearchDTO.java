package com.example.pal.dto;

import lombok.Data;

@Data
public class CourseSearchDTO {
    String title;
    String description;
    String categoryName;
    Boolean free;
    String difficulty;
}
