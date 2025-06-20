package com.cabrooster.controller;

import com.cabrooster.model.Passenger;
import com.cabrooster.repository.PassengerRepository;
import com.cabrooster.service.RateLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PassengerController.class)
@ActiveProfiles("test")
public class PassengerOnlyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PassengerRepository passengerRepository;

    @MockBean
    private RateLimitService rateLimitService;

    private final Long passengerId = 1L;
    private final String testIp = "127.0.0.1";

    @BeforeEach
    public void setup() {
        // Setup test passenger
        Passenger passenger = new Passenger();
        passenger.setId(passengerId);
        passenger.setName("John Doe");
        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));

        // Setup rate limiter to allow all requests
        doNothing().when(rateLimitService).checkRateLimit(anyString());
    }

    @Test
    public void testGetPassengerById() throws Exception {
        mockMvc.perform(get("/api/passengers/{id}", passengerId)
                .header("X-Forwarded-For", testIp)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(passengerId))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }
}
