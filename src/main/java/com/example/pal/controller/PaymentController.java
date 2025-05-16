package com.example.pal.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.pal.service.PaymentService;
import com.example.pal.model.Payment;
import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody BigDecimal amount) {
        Payment payment = paymentService.createPayment(amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @PostMapping("/process/{id}")
    public ResponseEntity<Payment> processPayment(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        paymentService.processPayment(payment);
        return ResponseEntity.ok(payment);
    }
}
