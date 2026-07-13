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
        for (Note n : notes.stream().limit(1).collect(Collectors.toList())) {
        	String content = n.getContent();
        	if (content.length() > 400) {
        	    content = content.substring(0, 400) + "...";
        	}

        	context.append("NOTE - ")
        	       .append(n.getTitle())
        	       .append(":\n")
        	       .append(content)
        	       .append("\n\n");
            sources.add("Note: " + n.getTitle());
        }

        List<Snippet> snippets = snippetRepository.searchByKeyword(userId, question);
        for (Snippet s : snippets.stream().limit(1).collect(Collectors.toList())) {
        	String code = s.getCode();
        	if (code.length() > 300) {
        	    code = code.substring(0, 300) + "...";
        	}

        	context.append("SNIPPET - ")
        	       .append(s.getTitle())
        	       .append(" (")
        	       .append(s.getLanguage())
        	       .append("):\n")
        	       .append(code)
        	       .append("\n\n");
            sources.add("Snippet: " + s.getTitle());
        }

        List<SqlQuery> queries = sqlQueryRepository.searchByKeyword(userId, question);
        for (SqlQuery q : queries.stream().limit(1).collect(Collectors.toList())) {
        	String sql = q.getQuery();
        	if (sql.length() > 300) {
        	    sql = sql.substring(0, 300) + "...";
        	}

        	context.append("SQL - ")
        	       .append(q.getTitle())
        	       .append(":\n")
        	       .append(sql)
        	       .append("\n\n");
            sources.add("SQL: " + q.getTitle());
        }

        List<ApiCollectionItem> apis = apiCollectionRepository.searchByKeyword(userId, question);
        for (ApiCollectionItem a : apis.stream().limit(1).collect(Collectors.toList())) {
        	String desc = a.getDescription();
        	if (desc != null && desc.length() > 300) {
        	    desc = desc.substring(0, 300) + "...";
        	}

        	context.append("API - ")
        	       .append(a.getMethod())
        	       .append(" ")
        	       .append(a.getUrl())
        	       .append(":\n")
        	       .append(desc)
        	       .append("\n\n");
            sources.add("API: " + a.getMethod() + " " + a.getUrl());
        }

        return new RetrievedContext(context.toString(), sources);
    }

    public ChatResponse chat(ChatRequest request) {
        User user = getCurrentUser();
        rateLimitService.checkAndIncrement(user.getId(), "canvas_ai_chat");

        RetrievedContext retrieved = retrieveContext(user.getId(), request.getMessage());

        StringBuilder prompt = new StringBuilder();
        prompt.append("You are Canvas AI, an intelligent assistant inside CodeCanvas.\n");
        prompt.append("Your goal is to provide clear, accurate, and helpful answers like ChatGPT.\n\n");

        prompt.append("If relevant notes, snippets, SQL queries, or APIs from the user's knowledge base are provided:\n");
        prompt.append("- Use them as context, not as content to copy.\n");
        prompt.append("- Never paste the entire note or large blocks of text.\n");
        prompt.append("- Explain the answer naturally in your own words.\n");
        prompt.append("- Keep the response concise and easy to understand.\n");
        prompt.append("- Include examples or code only when useful or when the user requests them.\n");
        prompt.append("- Mention which saved item(s) you referred to at the end of your answer.\n\n");

        prompt.append("If no relevant saved content exists, answer using your general knowledge and mention that no related saved content was found.\n\n");

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