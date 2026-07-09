package com.example.codecanvas.repository;

import com.example.codecanvas.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserId(Long userId);
    List<Note> findByUserIdAndTitleContainingIgnoreCase(Long userId, String title);
    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND " +"(LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Note> searchByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
    List<Note> findByUserIdAndLastViewedBefore(Long userId, java.time.LocalDateTime cutoff);
}