package com.example.pal.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "enrollments")
@Data
public class Enrollment {

    @EmbeddedId
    private EnrollmentId id;

    @ManyToOne @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne @MapsId("courseId")
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private Date enrollmentDate;

    // Esta es la única propiedad Payment: relación correctamente mapeada
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;
}
