package com.cabrooster.config;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class MCPConfig {

    @Value("${spring.ai.openai.api-key:demo}")
    private String openAiApiKey;

    @Value("${spring.ai.openai.model:gpt-3.5-turbo}")
    private String model;
    
    @Value("classpath:/data/vectorstore.json")
    private Resource vectorStoreResource;

    @Bean
    public OpenAiApi openAiApi() {
        return new OpenAiApi(openAiApiKey);
    }

    @Bean
    public ChatClient chatClient(OpenAiApi openAiApi) {
        return new OpenAiChatClient(openAiApi);
    }

    @Bean
    public EmbeddingClient embeddingClient(OpenAiApi openAiApi) {
        return new OpenAiEmbeddingClient(openAiApi);
    }
    
    @Bean
    public VectorStore vectorStore(EmbeddingClient embeddingClient) {
        return new SimpleVectorStore(embeddingClient);
    }
}
