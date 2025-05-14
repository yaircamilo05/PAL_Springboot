package com.example.pal.dto;

import com.example.pal.model.PaymentStatus;

import lombok.Data;

@Data
public class EnrollmentDTO {
    private Long userId;
    private Long courseId;
    private String courseName;
    private PaymentStatus paymentStatus;
}