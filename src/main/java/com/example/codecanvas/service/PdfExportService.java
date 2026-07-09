package com.example.codecanvas.service;

import com.example.codecanvas.entity.Note;
import com.example.codecanvas.repository.NoteRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfExportService {

    @Autowired
    private NoteRepository noteRepository;

    public byte[] exportNoteToPdf(Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Font metaFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
            Font bodyFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

            Paragraph title = new Paragraph(note.getTitle(), titleFont);
            title.setSpacingAfter(6);
            document.add(title);

            Paragraph meta = new Paragraph("Type: " + note.getType() + "  |  Created: " + note.getCreatedAt(), metaFont);
            meta.setSpacingAfter(20);
            document.add(meta);

            Paragraph body = new Paragraph(note.getContent(), bodyFont);
            document.add(body);

            document.close();
            return out.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }
    }
}