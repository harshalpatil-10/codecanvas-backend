package com.example.codecanvas.service;

import com.example.codecanvas.entity.RateLimitEntry;
import com.example.codecanvas.repository.RateLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class RateLimitService {

    @Autowired
    private RateLimitRepository rateLimitRepository;

    private static final int DAILY_LIMIT = 20;

    public void checkAndIncrement(Long userId, String feature) {
        LocalDate today = LocalDate.now();
        RateLimitEntry entry = rateLimitRepository
                .findByUserIdAndFeatureAndDate(userId, feature, today)
                .orElse(new RateLimitEntry(userId, feature, today, 0));

        if (entry.getCount() >= DAILY_LIMIT) {
            throw new RuntimeException("Daily limit reached for " + feature + ". Try again tomorrow.");
        }

        entry.setCount(entry.getCount() + 1);
        rateLimitRepository.save(entry);
    }
}