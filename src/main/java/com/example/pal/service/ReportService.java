package com.example.pal.service;

import com.example.pal.dto.StudentProgressReportDTO;
import com.example.pal.model.*;
import com.example.pal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.util.List;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final EnrollmentRepository enrollmentRepository;
    private final ExamRepository examRepository;
    private final ExamAttemptRepository examAttemptRepository;

    /**
     * Genera la lista con el progreso del estudiante para un curso
     */
    public List<StudentProgressReportDTO> generateProgressReportForCourse(Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByIdCourseId(courseId);
        List<Exam> exams = examRepository.findByCourseId(courseId);

        List<StudentProgressReportDTO> reportList = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            User student = enrollment.getUser();
            List<ExamAttempt> attempts = examAttemptRepository.findByStudentId(student.getId());

            int totalExams = exams.size();
            int completedExams = 0;
            double totalScore = 0;
            int scoredExams = 0;

            for (Exam exam : exams) {
                Optional<ExamAttempt> bestAttempt = attempts.stream()
                    .filter(attempt -> attempt.getExam().getId().equals(exam.getId()) &&
                            (attempt.getStatus() == ExamStatus.SUBMITTED || attempt.getStatus() == ExamStatus.GRADED))
                    .max(Comparator.comparing(ExamAttempt::getScore));

                if (bestAttempt.isPresent()) {
                    completedExams++;
                    totalScore += bestAttempt.get().getScore();
                    scoredExams++;
                }
            }

            StudentProgressReportDTO dto = new StudentProgressReportDTO();
            dto.setStudentName(student.getUsername());
            dto.setExamCompletionPercentage(totalExams == 0 ? 0 : (completedExams * 100.0 / totalExams));
            dto.setAverageScore(scoredExams == 0 ? 0 : totalScore / scoredExams);

            reportList.add(dto);
        }

        return reportList;
    }

    /**
     * Genera el contenido CSV del reporte de progreso
     */
    public String generateProgressCsv(Long courseId) {
        List<StudentProgressReportDTO> report = generateProgressReportForCourse(courseId);
        StringBuilder sb = new StringBuilder();
        sb.append("Nombre del Estudiante,Porcentaje Completado,Promedio\n");

        for (StudentProgressReportDTO dto : report) {
            sb.append(dto.getStudentName()).append(",");
            sb.append(dto.getExamCompletionPercentage()).append("%,");
            sb.append(dto.getAverageScore()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Genera el contenido PDF del reporte de progreso
     */
    public byte[] generateProgressPdf(Long courseId) {
        List<StudentProgressReportDTO> report = generateProgressReportForCourse(courseId);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Reporte de Progreso del Curso", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.addCell("Estudiante");
            table.addCell("Porcentaje Completado");
            table.addCell("Promedio");

            for (StudentProgressReportDTO dto : report) {
                table.addCell(dto.getStudentName());
                table.addCell(dto.getExamCompletionPercentage() + "%");
                table.addCell(String.valueOf(dto.getAverageScore()));
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }
    }
}
