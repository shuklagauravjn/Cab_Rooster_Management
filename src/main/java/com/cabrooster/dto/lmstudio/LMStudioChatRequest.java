package com.cabrooster.dto.lmstudio;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LMStudioChatRequest {
    private String model;
    private List<Message> messages;
    private List<Function> functions;
    private Object function_call; // Can be "auto", "none", or {"name": "function_name"}
    private double temperature = 0.7;
    private int max_tokens = 2000;
    private boolean stream = false;

    public LMStudioChatRequest(String model, String prompt) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("user", prompt));
    }

    @Data
    public static class Message {
        private String role;
        private String content;
        private String name;
        private FunctionCall function_call;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    @Data
    public static class Function {
        private String name;
        private String description;
        private Object parameters;

        public Function(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        public Function(String name, String description, Object parameters) {
            this.name = name;
            this.description = description;
            this.parameters = parameters;
        }
    }
    
    @Data
    public static class FunctionCall {
        private String name;
        private String arguments;
        
        public FunctionCall(String name, String arguments) {
            this.name = name;
            this.arguments = arguments;
        }
    }
}
