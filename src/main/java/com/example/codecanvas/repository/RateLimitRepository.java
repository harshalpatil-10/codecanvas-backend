package com.example.codecanvas.repository;

import com.example.codecanvas.entity.RateLimitEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface RateLimitRepository extends JpaRepository<RateLimitEntry, Long> {
    Optional<RateLimitEntry> findByUserIdAndFeatureAndDate(Long userId, String feature, LocalDate date);
}