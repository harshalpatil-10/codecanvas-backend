package com.example.codecanvas.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_sessions")
public class InterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String topics; // comma-separated topic labels, e.g. "Spring Boot,JWT,SQL"
    private String difficulty; // Beginner, Intermediate, Advanced
    private String interviewType; // Technical, HR, Coding, Rapid Fire
    private int totalQuestions;
    private int questionsAsked;
    private String status; // IN_PROGRESS, COMPLETED
    private Double overallScore;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public InterviewSession() {}

    public InterviewSession(User user, String topics, String difficulty, String interviewType, int totalQuestions) {
        this.user = user;
        this.topics = topics;
        this.difficulty = difficulty;
        this.interviewType = interviewType;
        this.totalQuestions = totalQuestions;
        this.questionsAsked = 0;
        this.status = "IN_PROGRESS";
        this.startedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getTopics() { return topics; }
    public void setTopics(String topics) { this.topics = topics; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getInterviewType() { return interviewType; }
    public void setInterviewType(String interviewType) { this.interviewType = interviewType; }
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public int getQuestionsAsked() { return questionsAsked; }
    public void setQuestionsAsked(int questionsAsked) { this.questionsAsked = questionsAsked; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getOverallScore() { return overallScore; }
    public void setOverallScore(Double overallScore) { this.overallScore = overallScore; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}