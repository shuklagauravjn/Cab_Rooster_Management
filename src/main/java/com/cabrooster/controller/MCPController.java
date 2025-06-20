package com.cabrooster.controller;

import com.cabrooster.dto.BookingRequest;
import com.cabrooster.dto.BookingResponse;
import com.cabrooster.service.AIModelService;
import com.cabrooster.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Controller for the Model Control Plane (MCP) endpoints.
 * Provides APIs for interacting with the AI model and managing conversations.
 */
@RestController
@RequestMapping("/api/mcp")
@Tag(
    name = "Model Control Plane",
    description = "APIs for interacting with the AI model powering Cab Rooster Management System"
)
public class MCPController {
    
    private static final Logger logger = LoggerFactory.getLogger(MCPController.class);
    private static final String DEFAULT_MODEL = "llama";
    
    private final AIModelService aiModelService;
    private final BookingService bookingService;
    
    @Autowired
    public MCPController(AIModelService aiModelService, BookingService bookingService) {
        this.aiModelService = aiModelService;
        this.bookingService = bookingService;
    }
    
    /**
     * Chat with the AI model using a single message.
     *
     * @param request The chat request containing the message
     * @param model The AI model to use (defaults to gpt-3.5-turbo)
     * @return The AI's response
     */
    @PostMapping(
        value = "/chat",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Chat with the AI model",
        description = "Send a message to the AI model and get a response"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successful response",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = com.cabrooster.controller.MCPController.ChatResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request"
    )
    @ApiResponse(
        responseCode = "429",
        description = "Rate limit exceeded"
    )
    public ResponseEntity<ChatResponse> chat(
            @Valid @RequestBody ChatRequest request,
            @RequestParam(required = false, defaultValue = DEFAULT_MODEL) String model) {
        
        String message = request.getMessage();
        if (message == null || message.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message cannot be empty");
        }
        
        logger.info("Processing chat request for model: {}", model);
        String response = aiModelService.generateResponse(message);
        logger.debug("Generated response for message: {}", response);
        
        return ResponseEntity.ok(new ChatResponse(
            response,
            model,
            message,
            System.currentTimeMillis(),
            "text"
        ));
    }
    
    /**
     * Search for relevant documents based on a query.
     *
     * @param query The search query
     * @param limit Maximum number of results to return (default: 5)
     * @return List of matching document contents
     */
    @GetMapping("/search")
    @Operation(
        summary = "Search documentation",
        description = "Search the knowledge base for relevant information using natural language processing."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Search results retrieved successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = SearchResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid query parameter"
    )
    public ResponseEntity<SearchResponse> search(
            @RequestParam @NotBlank @Size(min = 3, max = 200) 
            @Parameter(description = "Search query text (3-200 characters)", required = true, example = "how to create a new user")
            String query,
            @RequestParam(defaultValue = "5")
            @Parameter(description = "Maximum number of results to return", example = "5")
            int limit) {
        
        logger.info("Processing search query: {} (limit: {})", query, limit);
        List<String> results = aiModelService.searchDocuments(query, limit);
        
        return ResponseEntity.ok(new SearchResponse(
            query,
            results,
            results.size(),
            System.currentTimeMillis()
        ));
    }
    
    /**
     * Add a new document to the knowledge base.
     *
     * @param request The document to add
     * @return Success/failure status
     */
    @PostMapping(
        value = "/documents",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Add a document to the knowledge base",
        description = "Add a document to the AI model's knowledge base for future reference"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Document added successfully"
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid document format"
    )
    public ResponseEntity<Map<String, Object>> addDocument(
            @Valid @RequestBody AddDocumentRequest request) {
        
        String content = request.getContent();
        // Validation is handled by the @Valid annotation and the DTO constraints
        
        logger.info("Adding new document to knowledge base");
        aiModelService.addDocumentAsync(content);
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Document added successfully",
            "timestamp", Instant.now().toString()
        ));
    }
    
    /**
     * Health check endpoint.
     *
     * @return Service health status
     */
    @GetMapping(
        path = "/health",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Health check",
        description = "Check the health status of the MCP service"
    )
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Model Control Plane",
            "version", "1.0.0",
            "timestamp", Instant.now().toString(),
            "model", DEFAULT_MODEL
        ));
    }
    
    /**
     * Book a new cab
     * 
     * @param request The booking request
     * @return Booking confirmation with details
     */
    @PostMapping(
        value = "/bookings",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Book a cab",
        description = "Create a new cab booking with the provided details"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Booking created successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = BookingResponse.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Invalid booking request"
    )
    public ResponseEntity<BookingResponse> bookCab(@Valid @RequestBody BookingRequest request) {
        logger.info("Received booking request: {}", request);
        BookingResponse response = bookingService.bookCab(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get booking status by ID
     * 
     * @param bookingId The booking ID
     * @return Current booking status and details
     */
    @GetMapping(
        value = "/bookings/{bookingId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Get booking status",
        description = "Retrieve the current status of a cab booking"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Booking details retrieved successfully",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = BookingResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Booking not found"
    )
    public ResponseEntity<BookingResponse> getBookingStatus(
            @PathVariable String bookingId) {
        logger.info("Fetching status for booking: {}", bookingId);
        BookingResponse response = bookingService.getBookingStatus(bookingId);
        return ResponseEntity.ok(response);
    }
    
    // Request/Response DTOs
    
    @Schema(description = "Chat request parameters")
    public static class ChatRequest {
        @Schema(description = "The message to send to the AI model", example = "Hello, how are you?", required = true)
        @NotBlank(message = "Message cannot be empty")
        @Size(min = 1, max = 1000, message = "Message must be between 1 and 1000 characters")
        private String message;

        @Schema(
            description = "Optional conversation ID for multi-turn conversations",
            example = "conv_1234567890"
        )
        private String conversationId;

        public ChatRequest() {}

        public ChatRequest(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getConversationId() {
            return conversationId;
        }

        public void setConversationId(String conversationId) {
            this.conversationId = conversationId;
        }
    }
    
    @Schema(description = "Response object for chat messages")
    public static class ChatResponse {
        @Schema(description = "The AI's response text", example = "The weather is sunny today!")
        private final String response;
        
        @Schema(description = "The AI model used to generate the response", example = "gpt-3.5-turbo")
        private final String model;
        
        @Schema(description = "The original prompt that was sent to the model", example = "What is the weather like today?")
        private final String prompt;
        
        @Schema(description = "Timestamp of when the response was generated (epoch millis)", example = "1678901234567")
        private final long timestamp;
        
        @Schema(description = "Type of the response", example = "text", allowableValues = {"text", "image", "code"})
        private final String responseType;

        public ChatResponse(String response, String model, String prompt, long timestamp, String responseType) {
            this.response = response;
            this.model = model;
            this.prompt = prompt;
            this.timestamp = timestamp;
            this.responseType = responseType;
        }

        public String getResponse() { return response; }
        public String getModel() { return model; }
        public String getPrompt() { return prompt; }
        public long getTimestamp() { return timestamp; }
        public String getResponseType() { return responseType; }
    }
    
    @Schema(description = "Request object for adding a new document")
    public static class AddDocumentRequest {
        @Schema(
            description = "Content of the document to add",
            required = true,
            minLength = 10,
            example = "This is a sample document content that will be added to the knowledge base."
        )
        @NotBlank(message = "Document content cannot be empty")
        private String content;

        @Schema(description = "Optional metadata for the document")
        private Map<String, Object> metadata;

        public AddDocumentRequest() {}

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    @Schema(description = "Response object for search results")
    public static class SearchResponse {
        @Schema(description = "The original search query", example = "how to reset password")
        private final String query;
        
        @Schema(description = "List of matching document snippets")
        private final List<String> results;
        
        @Schema(description = "Total number of results found", example = "5")
        private final int count;
        
        @Schema(description = "Timestamp of when the search was performed (epoch millis)", example = "1678901234567")
        private final long timestamp;
        
        public SearchResponse(String query, List<String> results, int count, long timestamp) {
            this.query = query;
            this.results = results != null ? results : List.of();
            this.count = count;
            this.timestamp = timestamp;
        }
        
        public String getQuery() { return query; }
        public List<String> getResults() { return results; }
        public int getCount() { return count; }
        public long getTimestamp() { return timestamp; }
    }
}
