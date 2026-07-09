package com.example.codecanvas.dto;

public class QuizResponse {
    private String noteTitle;
    private String quizText;

    public QuizResponse(String noteTitle, String quizText) {
        this.noteTitle = noteTitle;
        this.quizText = quizText;
    }

    public String getNoteTitle() { return noteTitle; }
    public String getQuizText() { return quizText; }
}