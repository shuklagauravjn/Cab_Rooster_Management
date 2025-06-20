package com.cabrooster.controller;

import com.cabrooster.repository.PassengerRepository;
import com.cabrooster.service.BookingService;
import com.cabrooster.service.LMStudioService;
import com.cabrooster.service.LocationService;
import com.cabrooster.service.RateLimitService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class MinimalTestConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public RateLimitService rateLimitService() {
        return mock(RateLimitService.class);
    }
    
    @Bean
    public BookingService bookingService() {
        return mock(BookingService.class);
    }
    
    @Bean
    public LMStudioService lmStudioService() {
        return mock(LMStudioService.class);
    }
    
    @Bean
    public LocationService locationService() {
        return mock(LocationService.class);
    }
    
    @Bean
    public PassengerRepository passengerRepository() {
        return mock(PassengerRepository.class);
    }
}
