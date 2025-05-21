package com.example.pal.controller;

import com.example.pal.dto.*;
import com.example.pal.model.*;
import com.example.pal.service.ExamService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



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
    public ResponseEntity<ExamAttemptDTO> startExam(
        @PathVariable("examId") Long examId, 
        @RequestParam("userId") Long userId) {
    ExamAttemptDTO attempt = examService.startExam(examId, userId);
    return ResponseEntity.ok(attempt);
}

@PostMapping("/submit/{examId}")
public ResponseEntity<ExamResultDTO> submitExam(
        @PathVariable("examId") Long examId,
        @RequestParam("userId") Long userId,
        @RequestBody ExamSubmissionDTO submission) {
    ExamResultDTO result = examService.submitExam(examId, userId, submission);
    return ResponseEntity.ok(result);
}

@GetMapping("/results/{examId}")
public ResponseEntity<ExamResultDTO> getExamResults(
        @PathVariable("examId") Long examId,
        @RequestParam("userId") Long userId) {
    ExamResultDTO result = examService.getExamResult(examId, userId);
    return ResponseEntity.ok(result);
}

@GetMapping("/{examId}/questions")
public ResponseEntity<List<QuestionDTO>> getExamQuestions(
        @PathVariable("examId") Long examId) {
    List<QuestionDTO> questions = examService.getExamQuestions(examId);
    return ResponseEntity.ok(questions);
}

}
