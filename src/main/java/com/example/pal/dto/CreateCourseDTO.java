package com.example.pal.dto;

import lombok.Data;

@Data
public class CreateCourseDTO {
    private String title;
    private String description;
    private float price;
    private Long categoryId;
    private Long instructorId;
}
