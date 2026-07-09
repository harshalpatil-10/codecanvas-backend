package com.example.codecanvas.service;

import com.example.codecanvas.dto.SearchResult;
import com.example.codecanvas.entity.*;
import com.example.codecanvas.repository.*;
import com.example.codecanvas.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    @Autowired private NoteRepository noteRepository;
    @Autowired private SnippetRepository snippetRepository;
    @Autowired private SqlQueryRepository sqlQueryRepository;
    @Autowired private ApiCollectionRepository apiCollectionRepository;
    @Autowired private UserRepository userRepository;

    public List<SearchResult> search(String keyword) {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();

        List<SearchResult> results = new ArrayList<>();

        for (Note n : noteRepository.searchByKeyword(userId, keyword)) {
            String preview = n.getContent().length() > 80 ? n.getContent().substring(0, 80) + "..." : n.getContent();
            results.add(new SearchResult("note", n.getId(), n.getTitle(), preview));
        }

        for (Snippet s : snippetRepository.searchByKeyword(userId, keyword)) {
            results.add(new SearchResult("snippet", s.getId(), s.getTitle(), s.getLanguage() + " · " + s.getTags()));
        }

        for (SqlQuery q : sqlQueryRepository.searchByKeyword(userId, keyword)) {
            results.add(new SearchResult("sql", q.getId(), q.getTitle(), q.getCategory()));
        }

        for (ApiCollectionItem a : apiCollectionRepository.searchByKeyword(userId, keyword)) {
            results.add(new SearchResult("api", a.getId(), a.getMethod() + " " + a.getUrl(), a.getDescription()));
        }

        return results;
    }
}