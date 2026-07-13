package com.example.codecanvas.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_collection", indexes = @Index(name = "idx_apicollection_user_id", columnList = "user_id"))
public class ApiCollectionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String method; // GET, POST, PUT, DELETE
    private String url;

    @Column(columnDefinition = "TEXT")
    private String headers;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(columnDefinition = "TEXT")
    private String exampleResponse;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime lastViewed;
    private boolean favorite = false;

    public ApiCollectionItem() {}

    public ApiCollectionItem(String method, String url, String headers, String body,
                              String exampleResponse, String description, User user) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.body = body;
        this.exampleResponse = exampleResponse;
        this.description = description;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.lastViewed = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getHeaders() { return headers; }
    public void setHeaders(String headers) { this.headers = headers; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getExampleResponse() { return exampleResponse; }
    public void setExampleResponse(String exampleResponse) { this.exampleResponse = exampleResponse; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastViewed() { return lastViewed; }
    public void setLastViewed(LocalDateTime lastViewed) { this.lastViewed = lastViewed; }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}