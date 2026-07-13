package com.example.codecanvas.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notes", indexes = @Index(name = "idx_note_user_id", columnList = "user_id"))
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String type; // e.g. "Java", "Spring", "SQL", "AWS"

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime lastViewed;

    private boolean favorite = false;

    // Optimistic locking - prevents silently overwriting concurrent edits
    @Version
    private Long version;

    public Note() {}

    public Note(String title, String content, String type, User user) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.lastViewed = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastViewed() { return lastViewed; }
    public void setLastViewed(LocalDateTime lastViewed) { this.lastViewed = lastViewed; }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}