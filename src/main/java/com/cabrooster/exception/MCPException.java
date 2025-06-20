package com.cabrooster.exception;

public class MCPException extends RuntimeException {
    private final String errorCode;
    
    public MCPException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
