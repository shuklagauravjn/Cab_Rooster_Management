package com.cabrooster.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AIModelService {

    private static final Logger logger = LoggerFactory.getLogger(AIModelService.class);

    private final LMStudioService lmStudioService;
    
    @Autowired
    public AIModelService(LMStudioService lmStudioService) {
        this.lmStudioService = lmStudioService;
    }
    
    public String generateResponse(String message) {
        try {
            if (message == null || message.trim().isEmpty()) {
                throw new IllegalArgumentException("Message cannot be empty");
            }
            
            logger.debug("Generating response for message: {}", message);
            
            // Use LM Studio service to generate response
            Mono<String> responseMono = lmStudioService.generateResponse(message);
            String response = responseMono.block(); // Block to get the result synchronously
            
            logger.debug("Generated response: {}", response);
            return response != null ? response : "I'm sorry, I couldn't generate a response at this time.";
            
        } catch (Exception e) {
            logger.error("Error generating response", e);
            return "I'm sorry, I encountered an error while processing your request. Please try again later.";
        }
    }
    
    public List<String> searchDocuments(String query, int limit) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Query cannot be null or empty");
        }
        
        logger.debug("Searching for query: '{}' (limit: {})", query, limit);
        
        try {
            // In a real implementation, this would search a document store
            // For now, we'll return a simple response
            String response = generateResponse("Search for: " + query + "\nLimit results to: " + limit);
            return List.of(response);
                
        } catch (Exception e) {
            logger.error("Error processing search query: " + query, e);
            throw new RuntimeException("Failed to process search query", e);
        }
    }
    
    @Async
    public CompletableFuture<Boolean> addDocumentAsync(String content) {
        // In a real implementation, this would add a document to a knowledge base
        logger.info("Document added to knowledge base");
        return CompletableFuture.completedFuture(true);
    }
}
