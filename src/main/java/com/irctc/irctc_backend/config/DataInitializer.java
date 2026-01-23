package com.irctc.irctc_backend.config;

import com.irctc.irctc_backend.entity.User;
import com.irctc.irctc_backend.entity.UserRole;
import com.irctc.irctc_backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner createDefaultAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            String adminEmail = "admin@irctc.com";

            if (userRepository.findByEmail(adminEmail).isEmpty()) {

                User admin = new User();
                admin.setName("IRCTC Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("Admin@123"));
                admin.setRole(UserRole.ADMIN);


                userRepository.save(admin);

                System.out.println("Default ADMIN created: admin@irctc.com / Admin@123");
            } else {
                System.out.println("ADMIN already exists");
            }
        };
    }
}
