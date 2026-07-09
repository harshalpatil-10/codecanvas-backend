package com.example.codecanvas.controller;

import com.example.codecanvas.service.PdfExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/export")
public class PdfExportController {

    @Autowired
    private PdfExportService pdfExportService;

    @GetMapping("/note/{id}")
    public ResponseEntity<byte[]> exportNote(@PathVariable Long id) {
        byte[] pdfBytes = pdfExportService.exportNoteToPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "note-" + id + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}