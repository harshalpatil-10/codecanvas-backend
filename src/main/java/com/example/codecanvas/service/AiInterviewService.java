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

@Service
public class AiInterviewService {

    @Autowired private InterviewSessionRepository sessionRepository;
    @Autowired private InterviewQuestionRepository questionRepository;
    @Autowired private NoteRepository noteRepository;
    @Autowired private SnippetRepository snippetRepository;
    @Autowired private SqlQueryRepository sqlQueryRepository;
    @Autowired private UserRepository userRepository;

    @Value("${gemini.api.key}") private String apiKey;
    @Value("${gemini.api.url}") private String apiUrl;

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Builds one big text blob of everything the user selected, to ground the AI's questions in their real content
    private String buildContentContext(StartInterviewRequest req) {
        StringBuilder sb = new StringBuilder();
        if (req.getNoteIds() != null) {
            for (Long id : req.getNoteIds()) {
                noteRepository.findById(id).ifPresent(n ->
                    sb.append("NOTE (" + n.getType() + "): " + n.getTitle() + "\n" + n.getContent() + "\n\n"));
            }
        }
        if (req.getSnippetIds() != null) {
            for (Long id : req.getSnippetIds()) {
                snippetRepository.findById(id).ifPresent(s ->
                    sb.append("SNIPPET (" + s.getLanguage() + "): " + s.getTitle() + "\n" + s.getCode() + "\n\n"));
            }
        }
        if (req.getSqlIds() != null) {
            for (Long id : req.getSqlIds()) {
                sqlQueryRepository.findById(id).ifPresent(q ->
                    sb.append("SQL (" + q.getCategory() + "): " + q.getTitle() + "\n" + q.getQuery() + "\n\n"));
            }
        }
        return sb.toString();
    }

    private JsonNode callGemini(String prompt) {
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
            String rawText = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            // Gemini sometimes wraps JSON in ```json ... ``` fences - strip those if present
            String cleaned = rawText.replaceAll("```json", "").replaceAll("```", "").trim();
            return mapper.readTree(cleaned);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response: " + e.getMessage());
        }
    }

    public InterviewTurnResponse startInterview(StartInterviewRequest req) {
        User user = getCurrentUser();
        String contentContext = buildContentContext(req);

        if (contentContext.trim().isEmpty()) {
            throw new RuntimeException("Select at least one note, snippet, or SQL query to base the interview on.");
        }

        String topicsLabel = "selected content";
        InterviewSession session = new InterviewSession(user, topicsLabel, req.getDifficulty(), req.getInterviewType(), req.getQuestionCount());
        sessionRepository.save(session);

        String prompt = "You are conducting a " + req.getDifficulty() + " level " + req.getInterviewType() +
                " mock interview based ONLY on the following material:\n\n" + contentContext +
                "\n\nAsk the first interview question. Respond ONLY in raw JSON, no markdown fences, in this exact shape: " +
                "{\"question\": \"...\", \"topic\": \"short topic label\"}";

        JsonNode result = callGemini(prompt);
        String question = result.path("question").asText();
        String topic = result.path("topic").asText();

        InterviewQuestion q = new InterviewQuestion(session, 1, question, topic);
        questionRepository.save(q);
        session.setQuestionsAsked(1);
        sessionRepository.save(session);

        InterviewTurnResponse resp = new InterviewTurnResponse();
        resp.setSessionId(session.getId());
        resp.setQuestionId(q.getId());
        resp.setQuestion(question);
        resp.setTopic(topic);
        resp.setCompleted(false);
        return resp;
    }

    public InterviewTurnResponse submitAnswer(Long sessionId, AnswerSubmitRequest req) {
        InterviewSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        List<InterviewQuestion> questions = questionRepository.findBySessionIdOrderByOrderIndexAsc(sessionId);
        InterviewQuestion current = questions.get(questions.size() - 1);
        current.setUserAnswer(req.getAnswer());

        // Build conversation history so Gemini can adapt based on everything asked/answered so far
        StringBuilder history = new StringBuilder();
        for (InterviewQuestion q : questions) {
            history.append("Q (" + q.getTopic() + "): " + q.getQuestionText() + "\n");
            if (q.getUserAnswer() != null) history.append("A: " + q.getUserAnswer() + "\n\n");
        }

        boolean isLastQuestion = session.getQuestionsAsked() >= session.getTotalQuestions();

        String prompt = "You are evaluating an interview answer.\n\nConversation so far:\n" + history +
                "\n\nEvaluate the LAST answer given. Respond ONLY in raw JSON, no markdown fences, in this exact shape: " +
                "{\"score\": <0-10 number>, \"strengths\": \"short bullet-style text\", \"weaknesses\": \"short bullet-style text\", " +
                "\"suggestedAnswer\": \"a strong model answer\"" +
                (isLastQuestion ? "}" :
                ", \"nextQuestion\": \"the next interview question - if the last answer was weak, ask a related follow-up on the same topic instead of moving on; otherwise move to a new topic\", \"nextTopic\": \"short topic label\"}");

        JsonNode result = callGemini(prompt);

        current.setScore(result.path("score").asDouble());
        current.setStrengths(result.path("strengths").asText());
        current.setWeaknesses(result.path("weaknesses").asText());
        current.setSuggestedAnswer(result.path("suggestedAnswer").asText());
        questionRepository.save(current);

        InterviewTurnResponse resp = new InterviewTurnResponse();
        resp.setSessionId(sessionId);
        resp.setScore(current.getScore());
        resp.setStrengths(current.getStrengths());
        resp.setWeaknesses(current.getWeaknesses());
        resp.setSuggestedAnswer(current.getSuggestedAnswer());

        if (isLastQuestion) {
            double avg = questions.stream().mapToDouble(q -> q.getScore() != null ? q.getScore() : 0).average().orElse(0);
            session.setOverallScore(avg);
            session.setStatus("COMPLETED");
            session.setCompletedAt(java.time.LocalDateTime.now());
            sessionRepository.save(session);

            resp.setCompleted(true);
            resp.setOverallScore(avg);
        } else {
            String nextQuestion = result.path("nextQuestion").asText();
            String nextTopic = result.path("nextTopic").asText();

            InterviewQuestion nextQ = new InterviewQuestion(session, session.getQuestionsAsked() + 1, nextQuestion, nextTopic);
            questionRepository.save(nextQ);
            session.setQuestionsAsked(session.getQuestionsAsked() + 1);
            sessionRepository.save(session);

            resp.setQuestionId(nextQ.getId());
            resp.setQuestion(nextQuestion);
            resp.setTopic(nextTopic);
            resp.setCompleted(false);
        }

        return resp;
    }

    public List<InterviewQuestion> getReport(Long sessionId) {
        return questionRepository.findBySessionIdOrderByOrderIndexAsc(sessionId);
    }

    public List<InterviewSession> getHistory() {
        User user = getCurrentUser();
        return sessionRepository.findByUserIdOrderByStartedAtDesc(user.getId());
    }
}