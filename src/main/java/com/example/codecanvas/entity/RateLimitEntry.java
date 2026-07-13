package com.example.codecanvas.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "rate_limit_entries")
public class RateLimitEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String feature;
    private LocalDate date;
    private int count;

    public RateLimitEntry() {}

    public RateLimitEntry(Long userId, String feature, LocalDate date, int count) {
        this.userId = userId;
        this.feature = feature;
        this.date = date;
        this.count = count;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFeature() { return feature; }
    public void setFeature(String feature) { this.feature = feature; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}