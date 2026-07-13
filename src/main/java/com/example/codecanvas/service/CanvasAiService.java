package com.example.codecanvas.service;

import com.example.codecanvas.dto.*;
import com.example.codecanvas.entity.*;
import com.example.codecanvas.repository.*;
import com.example.codecanvas.util.SecurityUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CanvasAiService {

    @Autowired private NoteRepository noteRepository;
    @Autowired private SnippetRepository snippetRepository;
    @Autowired private SqlQueryRepository sqlQueryRepository;
    @Autowired private ApiCollectionRepository apiCollectionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RateLimitService rateLimitService;

    @Value("${gemini.api.key}") private String apiKey;
    @Value("${gemini.api.url}") private String apiUrl;

    private static final int MAX_HISTORY_MESSAGES = 6;

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Retrieval step: pull the most relevant saved content for this question,
    // reusing the same search queries built for the Search feature.
    private RetrievedContext retrieveContext(Long userId, String question) {
        StringBuilder context = new StringBuilder();
        List<String> sources = new ArrayList<>();

        List<Note> notes = noteRepository.searchByKeyword(userId, question);
        for (Note n : notes.stream().limit(2).collect(Collectors.toList())) {
            context.append("NOTE - ").append(n.getTitle()).append(":\n").append(n.getContent()).append("\n\n");
            sources.add("Note: " + n.getTitle());
        }

        List<Snippet> snippets = snippetRepository.searchByKeyword(userId, question);
        for (Snippet s : snippets.stream().limit(2).collect(Collectors.toList())) {
            context.append("SNIPPET - ").append(s.getTitle()).append(" (").append(s.getLanguage()).append("):\n")
                    .append(s.getCode()).append("\n\n");
            sources.add("Snippet: " + s.getTitle());
        }

        List<SqlQuery> queries = sqlQueryRepository.searchByKeyword(userId, question);
        for (SqlQuery q : queries.stream().limit(2).collect(Collectors.toList())) {
            context.append("SQL - ").append(q.getTitle()).append(":\n").append(q.getQuery()).append("\n\n");
            sources.add("SQL: " + q.getTitle());
        }

        List<ApiCollectionItem> apis = apiCollectionRepository.searchByKeyword(userId, question);
        for (ApiCollectionItem a : apis.stream().limit(2).collect(Collectors.toList())) {
            context.append("API - ").append(a.getMethod()).append(" ").append(a.getUrl()).append(":\n")
                    .append(a.getDescription()).append("\n\n");
            sources.add("API: " + a.getMethod() + " " + a.getUrl());
        }

        return new RetrievedContext(context.toString(), sources);
    }

    public ChatResponse chat(ChatRequest request) {
        User user = getCurrentUser();
        rateLimitService.checkAndIncrement(user.getId(), "canvas_ai_chat");

        RetrievedContext retrieved = retrieveContext(user.getId(), request.getMessage());

        StringBuilder prompt = new StringBuilder();
        prompt.append("You are Canvas AI, a helpful assistant inside a developer's personal knowledge tool called CodeCanvas.\n");
        prompt.append("Answer the user's question. If relevant content from their saved notes/snippets/SQL/APIs is provided below, ")
              .append("ground your answer in it and mention which item you used. If nothing relevant is provided, answer generally ")
              .append("using your own knowledge, and say you didn't find anything saved on this topic.\n\n");

        if (!retrieved.context.trim().isEmpty()) {
            prompt.append("--- User's relevant saved content ---\n").append(retrieved.context).append("--- end ---\n\n");
        }

        if (request.getHistory() != null) {
            int start = Math.max(0, request.getHistory().size() - MAX_HISTORY_MESSAGES);
            for (ChatMessageDto msg : request.getHistory().subList(start, request.getHistory().size())) {
                prompt.append(msg.getRole().equals("user") ? "User: " : "Assistant: ").append(msg.getContent()).append("\n");
            }
        }

        prompt.append("User: ").append(request.getMessage()).append("\nAssistant:");

        String answer = callGemini(prompt.toString());
        return new ChatResponse(answer, retrieved.sources);
    }

    private String callGemini(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-goog-api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);
        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(textPart));
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(content));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response: " + e.getMessage());
        }
    }

    private static class RetrievedContext {
        String context;
        List<String> sources;
        RetrievedContext(String context, List<String> sources) {
            this.context = context;
            this.sources = sources;
        }
    }
}