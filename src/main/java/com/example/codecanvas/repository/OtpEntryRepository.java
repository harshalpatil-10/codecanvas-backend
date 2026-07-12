package com.example.codecanvas.repository;

import com.example.codecanvas.entity.OtpEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpEntryRepository extends JpaRepository<OtpEntry, Long> {
    Optional<OtpEntry> findByEmail(String email);
    void deleteByEmail(String email);
}