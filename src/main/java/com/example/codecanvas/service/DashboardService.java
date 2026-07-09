package com.example.codecanvas.service;

import com.example.codecanvas.dto.DashboardStats;
import com.example.codecanvas.dto.TimelineItem;
import com.example.codecanvas.entity.*;
import com.example.codecanvas.repository.*;
import com.example.codecanvas.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DashboardService {

    @Autowired private NoteRepository noteRepository;
    @Autowired private SnippetRepository snippetRepository;
    @Autowired private SqlQueryRepository sqlQueryRepository;
    @Autowired private ApiCollectionRepository apiCollectionRepository;
    @Autowired private UserRepository userRepository;

    private static final int REVISION_THRESHOLD_DAYS = 30;

    private Long getCurrentUserId() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    public DashboardStats getStats() {
        Long userId = getCurrentUserId();
        LocalDateTime cutoff = LocalDateTime.now().minusDays(REVISION_THRESHOLD_DAYS);

        long revisionDue = noteRepository.findByUserIdAndLastViewedBefore(userId, cutoff).size()
                + snippetRepository.findByUserIdAndLastViewedBefore(userId, cutoff).size();

        return new DashboardStats(
                snippetRepository.findByUserId(userId).size(),
                noteRepository.findByUserId(userId).size(),
                apiCollectionRepository.findByUserId(userId).size(),
                sqlQueryRepository.findByUserId(userId).size(),
                revisionDue
        );
    }

    public List<TimelineItem> getTimeline() {
        Long userId = getCurrentUserId();
        List<TimelineItem> timeline = new ArrayList<>();

        for (Note n : noteRepository.findByUserId(userId))
            timeline.add(new TimelineItem("Note", n.getTitle(), n.getCreatedAt()));

        for (Snippet s : snippetRepository.findByUserId(userId))
            timeline.add(new TimelineItem("Snippet", s.getTitle(), s.getCreatedAt()));

        for (SqlQuery q : sqlQueryRepository.findByUserId(userId))
            timeline.add(new TimelineItem("SQL Query", q.getTitle(), q.getCreatedAt()));

        for (ApiCollectionItem a : apiCollectionRepository.findByUserId(userId))
            timeline.add(new TimelineItem("API", a.getMethod() + " " + a.getUrl(), a.getCreatedAt()));

        timeline.sort(Comparator.comparing(TimelineItem::getTimestamp).reversed());
        return timeline;
    }

    public List<Object> getRevisionDueItems() {
        Long userId = getCurrentUserId();
        LocalDateTime cutoff = LocalDateTime.now().minusDays(REVISION_THRESHOLD_DAYS);

        List<Object> due = new ArrayList<>();
        due.addAll(noteRepository.findByUserIdAndLastViewedBefore(userId, cutoff));
        due.addAll(snippetRepository.findByUserIdAndLastViewedBefore(userId, cutoff));
        return due;
    }
}