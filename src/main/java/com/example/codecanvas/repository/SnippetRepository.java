package com.example.codecanvas.repository;

import com.example.codecanvas.entity.Snippet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SnippetRepository extends JpaRepository<Snippet, Long> {
    List<Snippet> findByUserId(Long userId);
    List<Snippet> findByUserIdAndLanguageIgnoreCase(Long userId, String language);
    @Query("SELECT s FROM Snippet s WHERE s.user.id = :userId AND " +
    	       "(LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.tags) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    	List<Snippet> searchByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
    List<Snippet> findByUserIdAndLastViewedBefore(Long userId, java.time.LocalDateTime cutoff);
}