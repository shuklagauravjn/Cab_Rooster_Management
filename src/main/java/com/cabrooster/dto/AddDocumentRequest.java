package com.cabrooster.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

public class AddDocumentRequest {
    @Schema(description = "The content of the document to add", example = "This is a test document about cab scheduling.", required = true, minLength = 10)
    private String content;
    
    @Schema(description = "Optional metadata for the document")
    private Map<String, String> metadata;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
