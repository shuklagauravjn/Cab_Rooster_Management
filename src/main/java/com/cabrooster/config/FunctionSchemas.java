package com.cabrooster.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FunctionSchemas {
    
    private static final String BOOK_CAB_SCHEMA_PATH = "function-schemas/book_cab.json";
    
    private FunctionSchemas() {
        // Private constructor to prevent instantiation
    }
    
    public static Map<String, String> loadSchemas() throws IOException {
        Map<String, String> schemas = new HashMap<>();
        Resource bookCabSchema = new ClassPathResource(BOOK_CAB_SCHEMA_PATH);
        
        // Load book_cab schema
        String bookCabJson = new String(bookCabSchema.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        schemas.put("book_cab", bookCabJson);
        
        return schemas;
    }
}
