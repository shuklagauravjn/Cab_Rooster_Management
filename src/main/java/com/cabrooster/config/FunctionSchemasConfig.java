package com.cabrooster.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;

@Configuration
public class FunctionSchemasConfig {

    @Bean
    public Map<String, String> functionSchemas() throws IOException {
        return FunctionSchemas.loadSchemas();
    }
}
