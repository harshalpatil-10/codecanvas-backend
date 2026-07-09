package com.example.codecanvas.dto;

import java.time.LocalDateTime;

public class TimelineItem {
    private String type;
    private String title;
    private LocalDateTime timestamp;

    public TimelineItem(String type, String title, LocalDateTime timestamp) {
        this.type = type;
        this.title = title;
        this.timestamp = timestamp;
    }

    public String getType() { return type; }
    public String getTitle() { return title; }
    public LocalDateTime getTimestamp() { return timestamp; }
}