package com.example.codecanvas.controller;

import com.example.codecanvas.dto.NoteRequest;
import com.example.codecanvas.entity.Note;
import com.example.codecanvas.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @PostMapping
    public Note createNote(@RequestBody NoteRequest request) {
        return noteService.createNote(request);
    }

    @GetMapping
    public List<Note> getAllNotes() {
        return noteService.getAllNotes();
    }

    @GetMapping("/{id}")
    public Note getNote(@PathVariable Long id) {
        return noteService.getNoteById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable Long id, @RequestBody NoteRequest request,
                                         @RequestParam Long version) {
        try {
            Note updated = noteService.updateNote(id, request, version);
            return ResponseEntity.ok(updated);
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            return ResponseEntity.status(409).body("This note was modified by another session. Please refresh and try again.");
        }
    }

    @DeleteMapping("/{id}")
    public String deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return "Note deleted successfully";
    }

    @PostMapping("/{id}/favorite")
    public Note toggleFavorite(@PathVariable Long id) {
        return noteService.toggleFavorite(id);
    }
}