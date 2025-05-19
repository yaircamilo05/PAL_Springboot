package com.example.pal.model;

public enum ExamStatus {
    IN_PROGRESS,       // Examen iniciado pero aún no enviado
    SUBMITTED,         // Examen enviado y calificado automáticamente
    GRADED,            // Examen calificado manualmente (para preguntas tipo ESSAY)
    EXPIRED            // El tiempo del examen expiró sin envío
}
