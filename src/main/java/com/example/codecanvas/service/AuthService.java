package com.example.codecanvas.service;

import com.example.codecanvas.dto.*;
import com.example.codecanvas.entity.OtpEntry;
import com.example.codecanvas.entity.User;
import com.example.codecanvas.repository.OtpEntryRepository;
import com.example.codecanvas.repository.UserRepository;
import com.example.codecanvas.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OtpEntryRepository otpEntryRepository;

    @Autowired
    private EmailService emailService;

    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );
        user.setVerified(false);
        userRepository.save(user);

        generateAndSendOtp(request.getEmail());
        return "OTP sent to your email. Please verify to complete registration.";
    }

    private void generateAndSendOtp(String email) {
        otpEntryRepository.deleteByEmail(email);
        String otp = String.format("%06d", new Random().nextInt(999999));
        OtpEntry entry = new OtpEntry(email, otp, LocalDateTime.now().plusMinutes(10));
        otpEntryRepository.save(entry);
        emailService.sendOtp(email, otp);
    }

    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        OtpEntry entry = otpEntryRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No OTP found for this email. Please register again."));

        if (entry.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired. Please request a new one.");
        }
        if (!entry.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Incorrect OTP.");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setVerified(true);
        userRepository.save(user);
        otpEntryRepository.deleteByEmail(request.getEmail());

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getName(), user.getEmail());
    }

    public String resendOtp(ResendOtpRequest request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("No account found for this email.");
        }
        generateAndSendOtp(request.getEmail());
        return "A new OTP has been sent.";
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!user.isVerified()) {
            throw new RuntimeException("Please verify your email before logging in.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getName(), user.getEmail());
    }

    public AuthResponse updateProfile(UpdateProfileRequest request) {
        String currentEmail = com.example.codecanvas.util.SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getName(), user.getEmail());
    }

    public void changePassword(ChangePasswordRequest request) {
        String currentEmail = com.example.codecanvas.util.SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}