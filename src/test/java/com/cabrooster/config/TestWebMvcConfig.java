package com.cabrooster.config;

import com.cabrooster.repository.CabDriverRepository;
import com.cabrooster.repository.PassengerRepository;
import com.cabrooster.repository.RideAssignmentRepository;
import com.cabrooster.service.BookingService;
import com.cabrooster.service.LMStudioService;
import com.cabrooster.service.LocationService;
import com.cabrooster.service.RateLimitService;
import com.cabrooster.service.RideAssignmentService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class TestWebMvcConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Primary
    public BookingService bookingService() {
        return Mockito.mock(BookingService.class);
    }

    @Bean
    @Primary
    public LMStudioService lmStudioService() {
        return Mockito.mock(LMStudioService.class);
    }

    @Bean
    @Primary
    public LocationService locationService() {
        return Mockito.mock(LocationService.class);
    }
    
    @Bean
    @Primary
    public PassengerRepository passengerRepository() {
        return Mockito.mock(PassengerRepository.class);
    }
    
    @Bean
    @Primary
    public CabDriverRepository cabDriverRepository() {
        return Mockito.mock(CabDriverRepository.class);
    }
    
    @Bean
    @Primary
    public RideAssignmentRepository rideAssignmentRepository() {
        return Mockito.mock(RideAssignmentRepository.class);
    }
    
    @Bean
    @Primary
    public RateLimitService rateLimitService() {
        return Mockito.mock(RateLimitService.class);
    }
    
    @Bean
    @Primary
    public RideAssignmentService rideAssignmentService() {
        return Mockito.mock(RideAssignmentService.class);
    }
}
