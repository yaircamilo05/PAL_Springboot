package com.example.pal.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "enrollments")
@Data
public class Enrollment {

    @EmbeddedId
    private EnrollmentId id;

    @ManyToOne @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne @MapsId("courseId")
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore
    private Course course;

    @Column(nullable = true)
    private Date enrollmentDate;

    // Esta es la única propiedad Payment: relación correctamente mapeada
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    @JsonIgnore
    private Payment payment;
}
