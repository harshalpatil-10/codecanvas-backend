package com.example.codecanvas.repository;

import com.example.codecanvas.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {
    List<InterviewQuestion> findBySessionIdOrderByOrderIndexAsc(Long sessionId);
}