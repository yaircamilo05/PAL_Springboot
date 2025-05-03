package com.example.pal.model;

public enum PaymentStatus {
    PENDING,    // Pago pendiente
    COMPLETED,  // Pago completado/verificado
    FAILED,     // Pago rechazado
    CANCELLED   // Pago cancelado
}