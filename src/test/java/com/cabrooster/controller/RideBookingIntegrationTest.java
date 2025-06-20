package com.cabrooster.controller;

import com.cabrooster.model.CabDriver;
import com.cabrooster.model.Passenger;
import com.cabrooster.model.RideAssignment;
// Status values as defined in the RideAssignment class
import com.cabrooster.repository.CabDriverRepository;
import com.cabrooster.repository.PassengerRepository;
import com.cabrooster.repository.RideAssignmentRepository;
import com.cabrooster.service.BookingService;
import com.cabrooster.service.LMStudioService;
import com.cabrooster.service.LocationService;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.cabrooster.config.TestWebMvcConfig;

@WebMvcTest(controllers = {PassengerController.class, CabController.class, RideController.class})
@Import(TestWebMvcConfig.class)
@ActiveProfiles("test")
public class RideBookingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PassengerRepository passengerRepository;

    @MockBean
    private CabDriverRepository cabDriverRepository;

    @MockBean
    private RideAssignmentRepository rideAssignmentRepository;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private LMStudioService lmStudioService;

    @MockBean
    private LocationService locationService;

    @MockBean
    private RateLimitService rateLimitService;

    private final Long passengerId = 1L;
    private final Long cabDriverId = 1L;
    private final String testIp = "127.0.0.1";

    @BeforeEach
    void setUp() {
        // Create a test passenger
        Passenger passenger = new Passenger();
        passenger.setId(passengerId);
        passenger.setName("Test Passenger");
        passenger.setEmail("test@example.com");
        passenger.setPhone("1234567890");
        passenger.setNeedsRide(false);

        // Create a test cab driver
        CabDriver cabDriver = new CabDriver();
        cabDriver.setId(cabDriverId);
        cabDriver.setName("Test Driver");
        cabDriver.setEmail("driver@example.com");
        cabDriver.setPhone("9876543210");
        cabDriver.setLicenseNumber("DL123456");
        cabDriver.setCabNumber("KA01AB1234");
        cabDriver.setAvailable(true);

        RideAssignment ride1 = new RideAssignment();
        ride1.setId(1L);
        ride1.setStatus("ASSIGNED");
        
        RideAssignment ride2 = new RideAssignment();
        ride2.setId(2L);
        ride2.setStatus("IN_PROGRESS");

        // Mock repository responses
        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));
        when(cabDriverRepository.findById(cabDriverId)).thenReturn(Optional.of(cabDriver));
        when(passengerRepository.save(any(Passenger.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cabDriverRepository.save(any(CabDriver.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    public void testRequestRide() throws Exception {
        // Mock the passenger repository to return the test passenger
        Passenger passenger = new Passenger();
        passenger.setId(passengerId);
        passenger.setNeedsRide(false);

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));
        when(passengerRepository.save(any(Passenger.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Test requesting a ride
        mockMvc.perform(post("/api/passengers/{id}/request-ride", passengerId)
                .header("X-Forwarded-For", testIp)
                .param("currentLatitude", "12.9716")
                .param("currentLongitude", "77.5946")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.needsRide", is(true)));

        // Verify passenger was saved with needsRide=true
        verify(passengerRepository).save(argThat(p ->
            p.getId().equals(passengerId) && p.isNeedsRide()
        ));
    }

    @Test
    public void testGetPassenger() throws Exception {
        // Mock the passenger repository to return the test passenger
        Passenger passenger = new Passenger();
        passenger.setId(passengerId);
        passenger.setName("Test Passenger");
        passenger.setEmail("test@example.com");
        passenger.setPhone("1234567890");
        passenger.setNeedsRide(true);

        when(passengerRepository.findById(passengerId)).thenReturn(Optional.of(passenger));

        // Test getting passenger details
        mockMvc.perform(get("/api/passengers/{id}", passengerId)
                .header("X-Forwarded-For", testIp)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(passengerId.intValue())))
                .andExpect(jsonPath("$.name", is("Test Passenger")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.needsRide", is(true)));

        // Verify passenger was looked up by ID
        verify(passengerRepository).findById(passengerId);
    }

    @Test
    public void testUpdateCabLocation() throws Exception {
        // Mock the cab driver repository
        CabDriver cabDriver = new CabDriver();
        cabDriver.setId(cabDriverId);
        cabDriver.setName("Test Driver");
        cabDriver.setEmail("driver@example.com");
        cabDriver.setPhone("9876543210");
        cabDriver.setLicenseNumber("DL123456");
        cabDriver.setCabNumber("KA01AB1234");
        
        when(cabDriverRepository.findById(cabDriverId)).thenReturn(java.util.Optional.of(cabDriver));
        when(cabDriverRepository.save(any(CabDriver.class))).thenAnswer(invocation -> {
            CabDriver savedDriver = invocation.getArgument(0);
            // Update the cab driver with the new location
            cabDriver.setCurrentLatitude(savedDriver.getCurrentLatitude());
            cabDriver.setCurrentLongitude(savedDriver.getCurrentLongitude());
            return cabDriver;
        });

        // Test updating cab location
        mockMvc.perform(put("/api/cabs/{id}/location", cabDriverId)
                .header("X-Forwarded-For", testIp)
                .param("latitude", "12.9716")
                .param("longitude", "77.5946")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(cabDriverId.intValue())))
                .andExpect(jsonPath("$.currentLatitude", is(12.9716)))
                .andExpect(jsonPath("$.currentLongitude", is(77.5946)));
                
        // Verify cab driver was updated with new location
        verify(cabDriverRepository).save(argThat(driver -> 
            driver.getId().equals(cabDriverId) && 
            driver.getCurrentLatitude() == 12.9716 && 
            driver.getCurrentLongitude() == 77.5946
        ));
    }

    @Test
    public void testGetActiveRides() throws Exception {
        // Mock the cab driver repository
        CabDriver cabDriver = new CabDriver();
        cabDriver.setId(cabDriverId);
        cabDriver.setName("Test Driver");
        
        when(cabDriverRepository.findById(cabDriverId)).thenReturn(java.util.Optional.of(cabDriver));
        when(rideAssignmentRepository.findByCabDriverIdAndStatus(eq(cabDriverId), eq("ACTIVE"))).thenReturn(List.of());
        
        // Test getting active rides
        mockMvc.perform(get("/api/rides/cab/{cabDriverId}/active", cabDriverId)
                .header("X-Forwarded-For", testIp)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
