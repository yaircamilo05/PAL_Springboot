package com.example.pal.controller;

import com.example.pal.dto.CertificateResponseDTO;
import com.example.pal.model.Certificate;
import com.example.pal.service.CertificateService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping("/generate/{courseId}")
    public ResponseEntity<CertificateResponseDTO> generateCertificate(
            @PathVariable("courseId") Long courseId,
            @RequestParam("studentId") Long studentId) {
        return ResponseEntity.ok(certificateService.generateCertificate(courseId, studentId));
    }

    @GetMapping("/download/{certificateId}")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable("certificateId") Long certificateId) {
        Certificate certificate = certificateService.getCertificateById(certificateId);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Certificado de Finalización", font);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(certificate.getContentText()));

            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("certificado_" + certificate.getId() + ".pdf")
                    .build());

            return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CertificateResponseDTO>> listStudentCertificates(@PathVariable("studentId") Long studentId) {
        return ResponseEntity.ok(certificateService.getCertificatesForStudent(studentId));
    }
}
