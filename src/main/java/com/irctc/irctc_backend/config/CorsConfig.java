package com.irctc.irctc_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:2026",
                "https://rail-irctc-frontend.vercel.app",
                "https://rail-irctc-frontend-git-main-manishkumar0002s-projects.vercel.app"
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // allow all headers (simpler & safer)
        config.setAllowedHeaders(List.of("*"));

        // expose JWT header
        config.setExposedHeaders(List.of("Authorization"));

        // ❗ JWT header auth → NO cookies → keep false
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
