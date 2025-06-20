package com.cabrooster.controller;

import com.cabrooster.service.AIModelService;
import com.cabrooster.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class MCPControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private AIModelService aiModelService;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private MCPController mcpController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeEach
    void setUp() {
        // Initialize MockMvc with the controller
        mockMvc = MockMvcBuilders.standaloneSetup(mcpController).build();
    }

    @Test
    public void healthCheck_ShouldReturnOk() throws Exception {
        // No mocks needed for this test as it's a simple endpoint
        mockMvc.perform(get("/api/mcp/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.service", is("Model Control Plane")));
    }

    @Test
    public void search_WithValidQuery_ShouldReturnResults() throws Exception {
        // Setup mock response
        String mockResponse = "Search results for: cab scheduling";
        
        // Mock the searchDocuments method
        when(aiModelService.searchDocuments(anyString(), anyInt()))
            .thenReturn(List.of(mockResponse));

        // Execute the request
        mockMvc.perform(get("/api/mcp/search")
                .param("query", "cab scheduling")
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query", is("cab scheduling")))
                .andExpect(jsonPath("$.results", hasSize(1)))
                .andExpect(jsonPath("$.results[0]", containsString("cab scheduling")));
    }

    @Test
    public void chat_WithValidMessage_ShouldReturnResponse() throws Exception {
        // Setup test data
        String testMessage = "Hello, how are you?";
        String testResponse = "I'm doing well, thank you!";
        
        // Mock the AI service
        when(aiModelService.generateResponse(testMessage))
            .thenReturn(testResponse);

        // Create request
        Map<String, String> request = new HashMap<>();
        request.put("message", testMessage);

        // Execute and verify
        mockMvc.perform(post("/api/mcp/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response", is(testResponse)));
    }

    @Test
    public void addDocument_WithValidContent_ShouldReturnSuccess() throws Exception {
        // Setup test data
        String content = "This is a test document about cab scheduling.";
        Map<String, Object> request = new HashMap<>();
        request.put("content", content);
        
        // Mock the addDocumentAsync method
        when(aiModelService.addDocumentAsync(eq(content)))
            .thenReturn(CompletableFuture.completedFuture(true));

        // Execute and verify
        mockMvc.perform(post("/api/mcp/documents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("Document added successfully")));
    }

    @Test
    public void search_WithEmptyQuery_ShouldReturnBadRequest() throws Exception {
        // Execute and verify
        mockMvc.perform(get("/api/mcp/search")
                .param("query", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void chat_WithEmptyMessage_ShouldReturnBadRequest() throws Exception {
        // Setup test data with empty message
        Map<String, String> request = new HashMap<>();
        request.put("message", "");

        // Execute and verify
        mockMvc.perform(post("/api/mcp/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addDocument_WithEmptyContent_ShouldReturnBadRequest() throws Exception {
        // Setup test data with empty content
        Map<String, String> request = new HashMap<>();
        request.put("content", "");

        // Execute and verify
        mockMvc.perform(post("/api/mcp/documents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
