package com.example.pal.dto;

import lombok.Data;

@Data
public class CourseResponseDTO {
    private Long id;
    private String title;
    private String description;
    private float price;
    private CategoryDTO category;
    private UserResponseDTO instructor;
} 