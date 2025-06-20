package com.cabrooster.dto.lmstudio;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LMStudioChatResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    private String system_fingerprint;
    private String error;

    @Data
    public static class Choice {
        private int index;
        private Message message;
        @JsonProperty("finish_reason")
        private String finishReason;
        private Object logprobs;
    }

    @Data
    public static class Message {
        private String role;
        private String content;
        private FunctionCall function_call;
        
        public String getContent() {
            return content != null ? content : "";
        }
    }

    @Data
    public static class FunctionCall {
        private String name;
        private String arguments;
    }

    @Data
    public static class Usage {
        @JsonProperty("prompt_tokens")
        private int promptTokens;
        @JsonProperty("completion_tokens")
        private int completionTokens;
        @JsonProperty("total_tokens")
        private int totalTokens;
    }
    
    public String getResponseContent() {
        if (choices != null && !choices.isEmpty()) {
            Message message = choices.get(0).getMessage();
            if (message != null) {
                return message.getContent();
            }
        }
        return "";
    }
    
    public FunctionCall getFunctionCall() {
        if (choices != null && !choices.isEmpty()) {
            Message message = choices.get(0).getMessage();
            if (message != null) {
                return message.getFunction_call();
            }
        }
        return null;
    }
}
