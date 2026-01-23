package com.irctc.irctc_backend.controller;

import com.irctc.irctc_backend.entity.User;
import com.irctc.irctc_backend.entity.UserRole;
import com.irctc.irctc_backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/test")
public class UserTestController {

    private final UserRepository userRepository;

    public UserTestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/users")
    public User createUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("manish@test.com");
        user.setPassword("test123"); // not encoded (only for testing)
        user.setRole(UserRole.USER);
        return userRepository.save(user);
    }

    @GetMapping("/secure")
    public String secureApi() {
        return "This is a secure API endpoint accessible only to authenticated users.";
    }
    @GetMapping("/profile")
    public String profile(Authentication authentication) {
        return "Logged-in user: " + authentication.getName();
    }
}
