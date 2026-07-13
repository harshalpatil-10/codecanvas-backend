package com.example.codecanvas.dto;

import java.util.List;

public class ChatRequest {
    private String message;
    private List<ChatMessageDto> history;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<ChatMessageDto> getHistory() { return history; }
    public void setHistory(List<ChatMessageDto> history) { this.history = history; }
}