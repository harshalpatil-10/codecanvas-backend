package com.example.codecanvas.controller;

import com.example.codecanvas.dto.*;
import com.example.codecanvas.entity.*;
import com.example.codecanvas.service.AiInterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/interview")
public class AiInterviewController {

    @Autowired
    private AiInterviewService aiInterviewService;

    @PostMapping("/start")
    public InterviewTurnResponse start(@RequestBody StartInterviewRequest request) {
        return aiInterviewService.startInterview(request);
    }

    @PostMapping("/{sessionId}/answer")
    public InterviewTurnResponse answer(@PathVariable Long sessionId, @RequestBody AnswerSubmitRequest request) {
        return aiInterviewService.submitAnswer(sessionId, request);
    }

    @GetMapping("/{sessionId}/report")
    public List<InterviewQuestion> report(@PathVariable Long sessionId) {
        return aiInterviewService.getReport(sessionId);
    }

    @GetMapping("/history")
    public List<InterviewSession> history() {
        return aiInterviewService.getHistory();
    }
}