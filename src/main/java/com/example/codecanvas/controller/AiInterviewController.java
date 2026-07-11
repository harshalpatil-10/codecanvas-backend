package com.example.codecanvas.controller;

import com.example.codecanvas.dto.*;
import com.example.codecanvas.entity.*;
import com.example.codecanvas.service.AiInterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/interview")
public class AiInterviewController {

    @Autowired
    private AiInterviewService aiInterviewService;

    @PostMapping("/start")
    public ResponseEntity<?> start(@RequestBody StartInterviewRequest request) {
        try {
            return ResponseEntity.ok(aiInterviewService.startInterview(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<?> answer(@PathVariable Long sessionId, @RequestBody AnswerSubmitRequest request) {
        try {
            return ResponseEntity.ok(aiInterviewService.submitAnswer(sessionId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{sessionId}/report")
    public ResponseEntity<?> report(@PathVariable Long sessionId) {
        try {
            return ResponseEntity.ok(aiInterviewService.getReport(sessionId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> history() {
        try {
            return ResponseEntity.ok(aiInterviewService.getHistory());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}