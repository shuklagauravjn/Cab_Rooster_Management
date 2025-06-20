package com.cabrooster.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for cab booking")
public class BookingResponse {
    
    @Schema(description = "Unique booking ID", example = "b3d3e3f4-5g6h-7i8j-9k0l-1m2n3o4p5q6r")
    private String bookingId;
    
    @Schema(description = "Status of the booking", example = "CONFIRMED")
    private String status;
    
    @Schema(description = "Estimated time of arrival (ISO format)", example = "2025-06-20T09:45:00")
    private LocalDateTime estimatedArrivalTime;
    
    @Schema(description = "Estimated fare in local currency", example = "25.50")
    private double estimatedFare;
    
    @Schema(description = "Vehicle details")
    private VehicleDetails vehicle;
    
    @Schema(description = "Driver details")
    private DriverDetails driver;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleDetails {
        @Schema(description = "Vehicle make and model", example = "Toyota Camry")
        private String model;
        
        @Schema(description = "License plate number", example = "ABC123")
        private String licensePlate;
        
        @Schema(description = "Vehicle color", example = "White")
        private String color;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DriverDetails {
        @Schema(description = "Driver's name", example = "John Doe")
        private String name;
        
        @Schema(description = "Driver's contact number", example = "+1234567890")
        private String phoneNumber;
        
        @Schema(description = "Driver's rating", example = "4.8")
        private double rating;
    }
    
    // Helper method to create a sample response for testing
    public static BookingResponse createSample() {
        return BookingResponse.builder()
                .bookingId(UUID.randomUUID().toString())
                .status("CONFIRMED")
                .estimatedArrivalTime(LocalDateTime.now().plusMinutes(15))
                .estimatedFare(25.50)
                .vehicle(VehicleDetails.builder()
                        .model("Toyota Camry")
                        .licensePlate("ABC123")
                        .color("White")
                        .build())
                .driver(DriverDetails.builder()
                        .name("John Doe")
                        .phoneNumber("+1234567890")
                        .rating(4.8)
                        .build())
                .build();
    }
}
