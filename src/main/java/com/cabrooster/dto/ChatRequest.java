package com.cabrooster.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ChatRequest {
    @Schema(description = "The message to send to the AI model", example = "Hello, how are you?", required = true)
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
