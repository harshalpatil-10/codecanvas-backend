package com.example.codecanvas.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sql_queries", indexes = @Index(name = "idx_sqlquery_user_id", columnList = "user_id"))
public class Snippet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String language;

    @Column(columnDefinition = "TEXT")
    private String code;

    private String difficulty; // Easy, Medium, Hard
    private String tags; // comma-separated, e.g. "Array,Binary Search"

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime lastViewed;
    private boolean favorite = false;

    public Snippet() {}

    public Snippet(String title, String language, String code, String difficulty, String tags, User user) {
        this.title = title;
        this.language = language;
        this.code = code;
        this.difficulty = difficulty;
        this.tags = tags;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.lastViewed = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastViewed() { return lastViewed; }
    public void setLastViewed(LocalDateTime lastViewed) { this.lastViewed = lastViewed; }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}