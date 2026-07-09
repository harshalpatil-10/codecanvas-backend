package com.example.codecanvas.dto;

public class SearchResult {
    private String type; // "note", "snippet", "sql", "api"
    private Long id;
    private String title;
    private String snippet; // short preview text

    public SearchResult(String type, Long id, String title, String snippet) {
        this.type = type;
        this.id = id;
        this.title = title;
        this.snippet = snippet;
    }

    public String getType() { return type; }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getSnippet() { return snippet; }
}