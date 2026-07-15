package com.irctc.irctc_backend.service;

import com.irctc.irctc_backend.dto.RegisterRequest;
import com.irctc.irctc_backend.entity.User;
import com.irctc.irctc_backend.entity.UserRole;
import com.irctc.irctc_backend.exception.UserAlreadyExistsException;
import com.irctc.irctc_backend.repository.UserRepository;
import com.irctc.irctc_backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists");

        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);

        userRepository.save(user);
    }


    @org.springframework.transaction.annotation.Transactional
    public String login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email"));

        // Check if account is locked
        if (user.getLockTime() != null && user.getLockTime().isAfter(java.time.LocalDateTime.now())) {
            long minutesLeft = java.time.Duration.between(java.time.LocalDateTime.now(), user.getLockTime()).toMinutes() + 1;
            throw new RuntimeException("Account is locked. Try again in " + minutesLeft + " minutes.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            int attempts = user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0;
            attempts++;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= 5) {
                user.setLockTime(java.time.LocalDateTime.now().plusMinutes(15));
                userRepository.save(user);
                throw new RuntimeException("Account locked due to 5 failed login attempts. Please try again after 15 minutes.");
            }

            userRepository.save(user);
            throw new RuntimeException("Invalid password. Attempt " + attempts + " of 5.");
        }

        // Reset failed login attempts on success
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);

        return jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
    }
}
