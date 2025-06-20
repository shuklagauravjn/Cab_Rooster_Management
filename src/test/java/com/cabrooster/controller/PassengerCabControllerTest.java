package com.cabrooster.controller;

import com.cabrooster.config.TestWebMvcConfig;
import com.cabrooster.model.CabDriver;
import com.cabrooster.model.Passenger;
import com.cabrooster.repository.CabDriverRepository;
import com.cabrooster.repository.PassengerRepository;
import com.cabrooster.service.BookingService;
import com.cabrooster.service.LMStudioService;
import com.cabrooster.service.LocationService;
import com.cabrooster.service.RateLimitService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({
    PassengerController.class,
    CabController.class
})
@Import(TestWebMvcConfig.class)
@ActiveProfiles("test")
public class PassengerCabControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private CabDriverRepository cabDriverRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private LMStudioService lmStudioService;

    @Autowired
    private RateLimitService rateLimitService;

    @Autowired
    private LocationService locationService;

    private final Long passengerId = 1L;
    private final Long cabDriverId = 1L;
    private final String testIp = "127.0.0.1";

    @BeforeEach
    public void setup() {
        // Setup test passenger
        Passenger passenger = new Passenger();
        passenger.setId(passengerId);
        passenger.setName("John Doe");
        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));

        // Setup test cab driver
        CabDriver cabDriver = new CabDriver();
        cabDriver.setId(cabDriverId);
        cabDriver.setName("Jane Smith");
        when(cabDriverRepository.findById(cabDriverId)).thenReturn(Optional.of(cabDriver));

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

    @Test
    public void testGetCabDriverById() throws Exception {
        mockMvc.perform(get("/api/cabs/{id}", cabDriverId)
                .header("X-Forwarded-For", testIp)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cabDriverId))
                .andExpect(jsonPath("$.name").value("Jane Smith"));
    }
}
