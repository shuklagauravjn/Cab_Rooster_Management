package com.cabrooster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties
public class CabRoosterApplication {
    public static void main(String[] args) {
        // Set default LM Studio configuration if not set
        if (System.getProperty("lmstudio.api.base-url") == null) {
            System.setProperty("lmstudio.api.base-url", "http://127.0.0.1:1234");
        }
        if (System.getProperty("lmstudio.model") == null) {
            System.setProperty("lmstudio.model", "llama-3.2-1b-instruct");
        }
        
        SpringApplication.run(CabRoosterApplication.class, args);
    }
}
