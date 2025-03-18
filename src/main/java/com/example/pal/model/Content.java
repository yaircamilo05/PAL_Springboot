package com.example.pal.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "content")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = false)
    private String type; // Tipo de archivo (PDF, MP4, MP3, etc.)

    @Column(nullable = false, unique = false)
    private String url; // URL del archivo en Google Cloud Storage

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Curso al que pertenece el archivo
}
