package com.example.pal.controller;

import com.example.pal.dto.CertificateResponseDTO;
import com.example.pal.model.Certificate;
import com.example.pal.service.CertificateService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.io.InputStream;

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
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // ✅ Insertar imagen como logo o marca de agua
            InputStream imageStream = getClass().getResourceAsStream("/static/images/logo.png");
            if (imageStream != null) {
                Image image = Image.getInstance(imageStream.readAllBytes());
                image.setAbsolutePosition(200, 400); // posición en la página
                image.scaleAbsolute(200, 200); // tamaño
                image.setAlignment(Image.UNDERLYING); // detrás del texto
                image.setTransparency(new int[] { 0x00, 0x10 }); // semitransparente
                document.add(image);
            }

            // ✅ Título y contenido del certificado
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Certificado de Finalización", titleFont);
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
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CertificateResponseDTO>> listStudentCertificates(
            @PathVariable("studentId") Long studentId) {
        return ResponseEntity.ok(certificateService.getCertificatesForStudent(studentId));
    }
}
