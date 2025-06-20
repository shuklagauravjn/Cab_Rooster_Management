package com.cabrooster.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Request object for booking a cab")
public class BookingRequest {
    
    @NotBlank(message = "Pickup location is required")
    @Schema(description = "Pickup location address", example = "123 Main St, City")
    private String pickupLocation;
    
    @NotBlank(message = "Drop-off location is required")
    @Schema(description = "Drop-off location address", example = "456 Oak Ave, City")
    private String dropoffLocation;
    
    @NotNull(message = "Pickup time is required")
    @Schema(description = "Desired pickup time (ISO format)", example = "2025-06-20T10:00:00")
    private LocalDateTime pickupTime;
    
    @Schema(description = "Number of passengers (default: 1)", example = "2")
    private int passengers = 1;
    
    @Schema(description = "Special requests or notes", example = "Need a child seat")
    private String specialRequests;
}
