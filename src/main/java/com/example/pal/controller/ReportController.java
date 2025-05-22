package com.example.pal.controller;

import com.example.pal.dto.StudentProgressReportDTO;
import com.example.pal.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/progress/{courseId}")
    public ResponseEntity<List<StudentProgressReportDTO>> getProgressByCourse(@PathVariable("courseId") Long courseId) {
        List<StudentProgressReportDTO> report = reportService.generateProgressReportForCourse(courseId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/progress/{courseId}/csv")
    public ResponseEntity<String> exportProgressAsCSV(@PathVariable("courseId") Long courseId) {
        String csvContent = reportService.generateProgressCsv(courseId);
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=progreso_curso_" + courseId + ".csv")
            .header("Content-Type", "text/csv")
            .body(csvContent);
    }

    @GetMapping("/progress/{courseId}/pdf")
    public ResponseEntity<byte[]> exportProgressAsPDF(@PathVariable("courseId") Long courseId) {
        byte[] pdfContent = reportService.generateProgressPdf(courseId);
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=progreso_curso_" + courseId + ".pdf")
            .header("Content-Type", "application/pdf")
            .body(pdfContent);
    }
}
