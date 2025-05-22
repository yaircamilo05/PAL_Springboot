package com.example.pal.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CertificateResponseDTO {
    private Long id;
    private String studentName;
    private String courseTitle;
    private String contentText;
    private LocalDateTime issuedAt;
}
