package com.example.codecanvas.controller;

import com.example.codecanvas.dto.SearchResult;
import com.example.codecanvas.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public List<SearchResult> search(@RequestParam String keyword) {
        return searchService.search(keyword);
    }
}