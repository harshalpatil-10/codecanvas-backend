package com.example.codecanvas.controller;

import com.example.codecanvas.dto.ApiCollectionRequest;
import com.example.codecanvas.entity.ApiCollectionItem;
import com.example.codecanvas.service.ApiCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/collection")
public class ApiCollectionController {

    @Autowired
    private ApiCollectionService apiCollectionService;

    @PostMapping
    public ApiCollectionItem createItem(@RequestBody ApiCollectionRequest request) {
        return apiCollectionService.createItem(request);
    }

    @GetMapping
    public List<ApiCollectionItem> getAllItems() {
        return apiCollectionService.getAllItems();
    }

    @GetMapping("/{id}")
    public ApiCollectionItem getItem(@PathVariable Long id) {
        return apiCollectionService.getItemById(id);
    }

    @PutMapping("/{id}")
    public ApiCollectionItem updateItem(@PathVariable Long id, @RequestBody ApiCollectionRequest request) {
        return apiCollectionService.updateItem(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteItem(@PathVariable Long id) {
        apiCollectionService.deleteItem(id);
        return "API item deleted successfully";
    }

    @PostMapping("/{id}/favorite")
    public ApiCollectionItem toggleFavorite(@PathVariable Long id) {
        return apiCollectionService.toggleFavorite(id);
    }
}