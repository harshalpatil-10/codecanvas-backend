package com.example.codecanvas.service;

import com.example.codecanvas.dto.ApiCollectionRequest;
import com.example.codecanvas.entity.ApiCollectionItem;
import com.example.codecanvas.entity.User;
import com.example.codecanvas.repository.ApiCollectionRepository;
import com.example.codecanvas.repository.UserRepository;
import com.example.codecanvas.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApiCollectionService {

    @Autowired
    private ApiCollectionRepository apiCollectionRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public ApiCollectionItem createItem(ApiCollectionRequest request) {
        User user = getCurrentUser();
        ApiCollectionItem item = new ApiCollectionItem(
                request.getMethod(), request.getUrl(), request.getHeaders(),
                request.getBody(), request.getExampleResponse(), request.getDescription(), user
        );
        return apiCollectionRepository.save(item);
    }

    public List<ApiCollectionItem> getAllItems() {
        User user = getCurrentUser();
        return apiCollectionRepository.findByUserId(user.getId());
    }

    public ApiCollectionItem getItemById(Long id) {
        ApiCollectionItem item = apiCollectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("API item not found"));
        item.setLastViewed(LocalDateTime.now());
        return apiCollectionRepository.save(item);
    }

    public ApiCollectionItem updateItem(Long id, ApiCollectionRequest request) {
        ApiCollectionItem item = apiCollectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("API item not found"));
        item.setMethod(request.getMethod());
        item.setUrl(request.getUrl());
        item.setHeaders(request.getHeaders());
        item.setBody(request.getBody());
        item.setExampleResponse(request.getExampleResponse());
        item.setDescription(request.getDescription());
        return apiCollectionRepository.save(item);
    }

    public void deleteItem(Long id) {
        apiCollectionRepository.deleteById(id);
    }

    public ApiCollectionItem toggleFavorite(Long id) {
        ApiCollectionItem item = apiCollectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("API item not found"));
        item.setFavorite(!item.isFavorite());
        return apiCollectionRepository.save(item);
    }
}