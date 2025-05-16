package com.example.pal.service;

import com.example.pal.model.Payment;
import com.example.pal.model.PaymentStatus;
import com.example.pal.repository.PaymentRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    public Payment createPayment(BigDecimal amount) {
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PENDING); // Estado inicial del pago
        payment.setPaymentDate(null); // Fecha nula hasta que se procese el pago
        payment.setExpeditionDate(new java.util.Date()); // Fecha de expedición del pago
        return paymentRepository.save(payment); 
    }
    
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
    }

    public void processPayment(Payment payment) {
        
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaymentDate(new java.util.Date());
        paymentRepository.save(payment);
    }
}