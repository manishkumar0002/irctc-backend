package com.irctc.irctc_backend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RailwayApiConfig {

    @Value("${railway.api.key}")
    private String apiKey;

    @Value("${railway.api.host}")
    private String apiHost;

    @Value("${railway.api.base-url}")
    private String baseUrl;
}
