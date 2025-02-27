package com.example.pal.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class EnrollmentId implements java.io.Serializable {
    private Long userId;
    private Long courseId;
}
