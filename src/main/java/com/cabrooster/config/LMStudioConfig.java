package com.cabrooster.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class LMStudioConfig {
    
    @Value("${lmstudio.api.base-url:http://127.0.0.1:1234}")
    private String baseUrl;
    
    @Value("${lmstudio.model:claude-3-opus}")
    private String defaultModel;
    
    @Value("#{${lmstudio.function.schemas:{null:null}}}")
    private Map<String, String> functionSchemas = new HashMap<>();
    
    @PostConstruct
    public void init() {
        System.out.println("LMStudioConfig initialized with functionSchemas: " + functionSchemas);
    }
    
    @Bean(name = "lmStudioWebClient")
    public WebClient lmStudioWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
    
    @Bean
    public String lmStudioFunctionsConfig() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // Create a new map without null keys
            Map<String, String> validSchemas = new HashMap<>();
            if (functionSchemas != null) {
                functionSchemas.forEach((key, value) -> {
                    if (key != null) {
                        validSchemas.put(key, value);
                    }
                });
            }
            return validSchemas.isEmpty() ? "{}" : objectMapper.writeValueAsString(validSchemas);
        } catch (JsonProcessingException e) {
            // If serialization fails, return an empty JSON object
            return "{}";
        }
    }
    
    public String getDefaultModel() {
        return defaultModel;
    }
}
