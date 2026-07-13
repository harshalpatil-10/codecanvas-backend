package com.example.codecanvas.controller;

import com.example.codecanvas.dto.ChatRequest;
import com.example.codecanvas.service.CanvasAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/canvas-ai")
public class CanvasAiController {

    @Autowired
    private CanvasAiService canvasAiService;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequest request) {
        try {
            return ResponseEntity.ok(canvasAiService.chat(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}