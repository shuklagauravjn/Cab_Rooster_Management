package com.cabrooster.service;

import com.cabrooster.config.LMStudioConfig;
import com.cabrooster.dto.BookingRequest;
import com.cabrooster.dto.lmstudio.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class LMStudioService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final BookingService bookingService;
    private final LMStudioConfig lmStudioConfig;
    
    @Value("${lmstudio.api.chat-endpoint:/v1/chat/completions}")
    private String chatEndpoint;

    @Autowired
    public LMStudioService(
            @Qualifier("lmStudioWebClient") WebClient webClient,
            ObjectMapper objectMapper,
            BookingService bookingService,
            LMStudioConfig lmStudioConfig) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.bookingService = bookingService;
        this.lmStudioConfig = lmStudioConfig;
    }

    public Mono<String> generateResponse(String prompt) {
        LMStudioChatRequest request = createChatRequest(prompt);
        
        return webClient.post()
                .uri(chatEndpoint)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::handleChatResponse)
                .onErrorResume(e -> {
                    // Log the error and return a user-friendly message
                    System.err.println("Error calling LM Studio: " + e.getMessage());
                    return Mono.just("I'm sorry, I'm having trouble connecting to the AI service right now.");
                });
    }
    
    private LMStudioChatRequest createChatRequest(String prompt) {
        LMStudioChatRequest request = new LMStudioChatRequest(lmStudioConfig.getDefaultModel(), prompt);
        
        // Add function definitions
        request.setFunctions(Collections.singletonList(
            new LMStudioChatRequest.Function(
                "book_cab",
                "Book a cab with the specified details"
            )
        ));
        
        return request;
    }
    
    private Mono<String> handleChatResponse(String responseJson) {
        try {
            LMStudioChatResponse response = objectMapper.readValue(responseJson, LMStudioChatResponse.class);
            
            // Check if the model wants to call a function
            if (response.getFunctionCall() != null) {
                return handleFunctionCall(response.getFunctionCall());
            }
            
            // Otherwise, return the regular response
            return Mono.just(response.getResponseContent());
            
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to process LM Studio response", e));
        }
    }
    
    private Mono<String> handleFunctionCall(LMStudioChatResponse.FunctionCall functionCall) {
        try {
            switch (functionCall.getName()) {
                case "book_cab":
                    return handleBookCab(functionCall.getArguments());
                default:
                    return Mono.just("I'm sorry, I don't know how to handle that function.");
            }
        } catch (Exception e) {
            return Mono.just("I encountered an error processing your request: " + e.getMessage());
        }
    }
    
    private Mono<String> handleBookCab(String argumentsJson) {
        try {
            // Parse the function arguments
            Map<String, Object> args = objectMapper.readValue(argumentsJson, Map.class);
            
            // Create a booking request
            BookingRequest request = new BookingRequest();
            request.setPickupLocation((String) args.get("pickupLocation"));
            request.setDropoffLocation((String) args.get("dropoffLocation"));
            request.setPickupTime(java.time.LocalDateTime.parse((String) args.get("pickupTime")));
            
            if (args.containsKey("passengers")) {
                request.setPassengers((Integer) args.get("passengers"));
            }
            if (args.containsKey("specialRequests")) {
                request.setSpecialRequests((String) args.get("specialRequests"));
            }
            
            // Process the booking
            var response = bookingService.bookCab(request);
            
            // Format the response for the user
            String message = String.format(
                "Your cab has been booked! %n" +
                "Booking ID: %s%n" +
                "Status: %s%n" +
                "Estimated Arrival: %s%n" +
                "Vehicle: %s %s (%s)%n" +
                "Driver: %s (%s, Rating: %.1f)",
                response.getBookingId(),
                response.getStatus(),
                response.getEstimatedArrivalTime(),
                response.getVehicle().getColor(),
                response.getVehicle().getModel(),
                response.getVehicle().getLicensePlate(),
                response.getDriver().getName(),
                response.getDriver().getPhoneNumber(),
                response.getDriver().getRating()
            );
            
            return Mono.just(message);
            
        } catch (Exception e) {
            return Mono.just("I'm sorry, I couldn't complete your cab booking. Please try again later.");
        }
    }
}
