package com.irctc.irctc_backend.service;

import com.irctc.irctc_backend.entity.User;
import com.irctc.irctc_backend.entity.UserRole;
import com.irctc.irctc_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //  GOOGLE OAUTH USER HANDLER
    public User processOAuthUser(String email, String name) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail(email);
                    user.setName(name);
                    user.setRole(UserRole.USER);
                    user.setPassword("OAUTH2_USER");
                    return userRepository.save(user);
                });
    }

    // USED BY PROFILE API
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + email));
    }
}
