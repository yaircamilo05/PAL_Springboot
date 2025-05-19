package com.example.pal.model;

public enum QuestionType {
    MULTIPLE_CHOICE,   // Opción múltiple (una sola respuesta correcta)
    MULTIPLE_ANSWER,   // Respuesta múltiple (varias respuestas correctas posibles)
    TRUE_FALSE,        // Verdadero o falso
    SHORT_ANSWER,      // Respuesta corta (texto libre)
    ESSAY              // Respuesta larga (texto libre extenso, requiere evaluación manual)
}
