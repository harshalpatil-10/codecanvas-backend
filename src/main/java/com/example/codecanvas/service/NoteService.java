package com.example.codecanvas.service;

import com.example.codecanvas.dto.NoteRequest;
import com.example.codecanvas.entity.Note;
import com.example.codecanvas.entity.User;
import com.example.codecanvas.repository.NoteRepository;
import com.example.codecanvas.repository.UserRepository;
import com.example.codecanvas.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Note createNote(NoteRequest request) {
        User user = getCurrentUser();
        Note note = new Note(request.getTitle(), request.getContent(), request.getType(), user);
        return noteRepository.save(note);
    }

    public List<Note> getAllNotes() {
        User user = getCurrentUser();
        return noteRepository.findByUserId(user.getId());
    }

    public Note getNoteById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        note.setLastViewed(LocalDateTime.now());
        return noteRepository.save(note);
    }

    public Note updateNote(Long id, NoteRequest request, Long clientVersion) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        // Manually check the version the client last saw against the current DB version.
        // If they don't match, someone else edited this note in between - reject the update.
        if (!note.getVersion().equals(clientVersion)) {
            throw new ObjectOptimisticLockingFailureException(Note.class, id);
        }

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setType(request.getType());

        return noteRepository.save(note); // @Version bump happens here automatically
    }

    public void deleteNote(Long id) {
        noteRepository.deleteById(id);
    }

    public Note toggleFavorite(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        note.setFavorite(!note.isFavorite());
        return noteRepository.save(note);
    }
}