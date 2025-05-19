package com.example.pal.controller;

import com.example.pal.dto.*;
import com.example.pal.model.*;
import com.example.pal.service.ExamService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @PostMapping("/create")
    public ResponseEntity<Exam> createExam(@RequestBody CreateExamDTO examDTO) {
        return ResponseEntity.status(201).body(examService.createExam(examDTO));
    }

    @GetMapping("/{examId}")
    public ResponseEntity<Exam> getExamById(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.getExamById(examId));
    }
    
    @PostMapping("/{examId}/start")
    public ResponseEntity<ExamAttempt> startExam(
            @PathVariable Long examId, 
            @AuthenticationPrincipal UserDetails userDetails) {
        // Asumiendo que UserDetails tiene el ID del usuario como nombre de usuario
        Long userId = Long.parseLong(userDetails.getUsername());
        ExamAttempt attempt = examService.startExam(examId, userId);
        return ResponseEntity.ok(attempt);
    }
    
    @PostMapping("/submit/{examId}")
    public ResponseEntity<ExamResultDTO> submitExam(
            @PathVariable Long examId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ExamSubmissionDTO submission) {
        // Asumiendo que UserDetails tiene el ID del usuario como nombre de usuario
        Long userId = Long.parseLong(userDetails.getUsername());
        ExamResultDTO result = examService.submitExam(examId, userId, submission);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/results/{examId}")
    public ResponseEntity<ExamResultDTO> getExamResults(
            @PathVariable Long examId,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Asumiendo que UserDetails tiene el ID del usuario como nombre de usuario
        Long userId = Long.parseLong(userDetails.getUsername());
        ExamResultDTO result = examService.getExamResult(examId, userId);
        return ResponseEntity.ok(result);
    }
}
