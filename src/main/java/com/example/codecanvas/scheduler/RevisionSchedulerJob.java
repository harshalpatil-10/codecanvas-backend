package com.example.codecanvas.scheduler;

import com.example.codecanvas.entity.Note;
import com.example.codecanvas.entity.Snippet;
import com.example.codecanvas.repository.NoteRepository;
import com.example.codecanvas.repository.SnippetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling 
public class RevisionSchedulerJob {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private SnippetRepository snippetRepository;

    private static final int REVISION_THRESHOLD_DAYS = 30;

    // Runs once every day at 8 AM server time.
    // Cron format: second minute hour day month weekday
    @Scheduled(cron = "0 0 8 * * *")
    public void checkOverdueRevisions() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(REVISION_THRESHOLD_DAYS);

        List<Note> overdueNotes = noteRepository.findAll().stream()
                .filter(n -> n.getLastViewed().isBefore(cutoff))
                .collect(java.util.stream.Collectors.toList());

        List<Snippet> overdueSnippets = snippetRepository.findAll().stream()
                .filter(s -> s.getLastViewed().isBefore(cutoff))
                .collect(java.util.stream.Collectors.toList());

        for (Note n : overdueNotes) {
            System.out.println("[Revision Reminder] You haven't revised note \"" + n.getTitle() +
                    "\" in over " + REVISION_THRESHOLD_DAYS + " days.");
        }

        for (Snippet s : overdueSnippets) {
            System.out.println("[Revision Reminder] You haven't revised snippet \"" + s.getTitle() +
                    "\" in over " + REVISION_THRESHOLD_DAYS + " days.");
        }
    }

    // Runs every 60 seconds - just for quick testing/demo purposes so you don't have to wait until 8 AM
    /*@Scheduled(fixedRate = 60000)
    public void quickTestCheck() {
        System.out.println("[Scheduler heartbeat] Checking for overdue revisions at " + LocalDateTime.now());
    }*/
}