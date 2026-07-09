package com.example.codecanvas.controller;

import com.example.codecanvas.dto.SnippetRequest;
import com.example.codecanvas.entity.Snippet;
import com.example.codecanvas.service.SnippetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/snippets")
public class SnippetController {

    @Autowired
    private SnippetService snippetService;

    @PostMapping
    public Snippet createSnippet(@RequestBody SnippetRequest request) {
        return snippetService.createSnippet(request);
    }

    @GetMapping
    public List<Snippet> getAllSnippets() {
        return snippetService.getAllSnippets();
    }

    @GetMapping("/{id}")
    public Snippet getSnippet(@PathVariable Long id) {
        return snippetService.getSnippetById(id);
    }

    @PutMapping("/{id}")
    public Snippet updateSnippet(@PathVariable Long id, @RequestBody SnippetRequest request) {
        return snippetService.updateSnippet(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteSnippet(@PathVariable Long id) {
        snippetService.deleteSnippet(id);
        return "Snippet deleted successfully";
    }

    @PostMapping("/{id}/favorite")
    public Snippet toggleFavorite(@PathVariable Long id) {
        return snippetService.toggleFavorite(id);
    }
}