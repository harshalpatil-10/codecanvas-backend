package com.example.codecanvas.controller;

import com.example.codecanvas.dto.QuizResponse;
import com.example.codecanvas.service.AiQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/quiz")
public class AiQuizController {

    @Autowired
    private AiQuizService aiQuizService;

    @GetMapping("/{noteId}")
    public QuizResponse generateQuiz(@PathVariable Long noteId) {
        return aiQuizService.generateQuiz(noteId);
    }
}