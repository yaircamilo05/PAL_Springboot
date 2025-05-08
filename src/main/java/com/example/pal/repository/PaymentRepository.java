package com.example.pal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pal.model.Payment;
import com.example.pal.model.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Aquí puedes agregar métodos personalizados si es necesario
    // Por ejemplo, encontrar pagos por estado o usuario
    List<Payment> findByStatus(PaymentStatus status);
} 
