package com.example.codecanvas.service;

import com.example.codecanvas.dto.SnippetRequest;
import com.example.codecanvas.entity.Snippet;
import com.example.codecanvas.entity.User;
import com.example.codecanvas.repository.SnippetRepository;
import com.example.codecanvas.repository.UserRepository;
import com.example.codecanvas.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SnippetService {

    @Autowired
    private SnippetRepository snippetRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Snippet createSnippet(SnippetRequest request) {
        User user = getCurrentUser();
        Snippet snippet = new Snippet(
                request.getTitle(), request.getLanguage(), request.getCode(),
                request.getDifficulty(), request.getTags(), user
        );
        return snippetRepository.save(snippet);
    }

    public List<Snippet> getAllSnippets() {
        User user = getCurrentUser();
        return snippetRepository.findByUserId(user.getId());
    }

    public Snippet getSnippetById(Long id) {
        Snippet snippet = snippetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Snippet not found"));
        snippet.setLastViewed(LocalDateTime.now());
        return snippetRepository.save(snippet);
    }

    public Snippet updateSnippet(Long id, SnippetRequest request) {
        Snippet snippet = snippetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Snippet not found"));
        snippet.setTitle(request.getTitle());
        snippet.setLanguage(request.getLanguage());
        snippet.setCode(request.getCode());
        snippet.setDifficulty(request.getDifficulty());
        snippet.setTags(request.getTags());
        return snippetRepository.save(snippet);
    }

    public void deleteSnippet(Long id) {
        snippetRepository.deleteById(id);
    }

    public Snippet toggleFavorite(Long id) {
        Snippet snippet = snippetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Snippet not found"));
        snippet.setFavorite(!snippet.isFavorite());
        return snippetRepository.save(snippet);
    }
}