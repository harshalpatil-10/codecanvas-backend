package com.example.codecanvas.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sql_queries", indexes = @Index(name = "idx_sqlquery_user_id", columnList = "user_id"))
public class SqlQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // e.g. "Inner Join", "Window Functions"

    @Column(columnDefinition = "TEXT")
    private String query;

    private String category; // e.g. "Joins", "Window Functions", "CTE", "Indexes"

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime lastViewed;
    private boolean favorite = false;

    public SqlQuery() {}

    public SqlQuery(String title, String query, String category, User user) {
        this.title = title;
        this.query = query;
        this.category = category;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.lastViewed = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastViewed() { return lastViewed; }
    public void setLastViewed(LocalDateTime lastViewed) { this.lastViewed = lastViewed; }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}