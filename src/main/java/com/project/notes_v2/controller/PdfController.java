package com.project.notes_v2.controller;

import com.project.notes_v2.exception.FailedRequestException;
import com.project.notes_v2.service.PdfService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.Map;


@RestController
@AllArgsConstructor
@RequestMapping("/api/pdf")
public class PdfController  {

    private final PdfService pdfService;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generatePdf(@RequestBody Map<String, String> request) {

        try {
            ByteArrayOutputStream output = pdfService.convertHtmlToPdf(request);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=document.pdf");

            return ResponseEntity.ok()
                                 .headers(headers)
                                 .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                                 .body(output.toByteArray());
        } catch (FailedRequestException exception) {
            throw new FailedRequestException();
        }
    }

}
