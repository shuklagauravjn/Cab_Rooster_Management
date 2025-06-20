package com.cabrooster.config;

import com.cabrooster.service.BookingService;
import com.cabrooster.service.LMStudioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @MockBean
    private LMStudioService lmStudioService;

    @MockBean
    private BookingService bookingService;

    @Bean
    @Primary
    public WebClient lmStudioWebClient() {
        return WebClient.builder().build();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
