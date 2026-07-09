package com.example.codecanvas.dto;

public class ApiCollectionRequest {
    private String method;
    private String url;
    private String headers;
    private String body;
    private String exampleResponse;
    private String description;

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
}