package com.example.codecanvas.dto;

public class InterviewTurnResponse {
    private Long sessionId;
    private Long questionId;
    private String question;
    private String topic;
    private boolean completed;

    // populated only when returning feedback after an answer
    private Double score;
    private String strengths;
    private String weaknesses;
    private String suggestedAnswer;
    private Double overallScore;

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }
    public String getWeaknesses() { return weaknesses; }
    public void setWeaknesses(String weaknesses) { this.weaknesses = weaknesses; }
    public String getSuggestedAnswer() { return suggestedAnswer; }
    public void setSuggestedAnswer(String suggestedAnswer) { this.suggestedAnswer = suggestedAnswer; }
    public Double getOverallScore() { return overallScore; }
    public void setOverallScore(Double overallScore) { this.overallScore = overallScore; }
}