package com.cabrooster.controller;

import com.cabrooster.model.Passenger;
import com.cabrooster.repository.PassengerRepository;
import com.cabrooster.service.RateLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PassengerController.class)
@ActiveProfiles("test")
public class PassengerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PassengerRepository passengerRepository;

    @MockBean
    private RateLimitService rateLimitService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long passengerId = 1L;
    
    private final String testIp = "127.0.0.1";

    @Test
    public void testGetPassenger() throws Exception {
        // Create a test passenger
        Passenger passenger = new Passenger();
        passenger.setId(passengerId);
        passenger.setName("Test Passenger");
        passenger.setEmail("test@example.com");
        passenger.setPhone("1234567890");
        passenger.setNeedsRide(false);

        // Mock the repository and rate limit service
        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));
        // RateLimitService's checkRateLimit throws an exception if rate limited
        // No need to mock it since it's void and we want it to do nothing by default

        // Test the endpoint
        mockMvc.perform(get("/api/passengers/{id}", passengerId)
                .header("X-Forwarded-For", testIp)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(passengerId))
                .andExpect(jsonPath("$.name").value("Test Passenger"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.needsRide").value(false));
    }
}
