package com.example.codecanvas.dto;

import java.util.List;

public class StartInterviewRequest {
    private List<Long> noteIds;
    private List<Long> snippetIds;
    private List<Long> sqlIds;
    private String difficulty;
    private String interviewType;
    private int questionCount;

    public List<Long> getNoteIds() { return noteIds; }
    public void setNoteIds(List<Long> noteIds) { this.noteIds = noteIds; }
    public List<Long> getSnippetIds() { return snippetIds; }
    public void setSnippetIds(List<Long> snippetIds) { this.snippetIds = snippetIds; }
    public List<Long> getSqlIds() { return sqlIds; }
    public void setSqlIds(List<Long> sqlIds) { this.sqlIds = sqlIds; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getInterviewType() { return interviewType; }
    public void setInterviewType(String interviewType) { this.interviewType = interviewType; }
    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }
}