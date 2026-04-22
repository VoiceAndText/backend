package com.quadcore.voiceandtext.common.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private String details;

    public ErrorResponse(String code, String message, String details) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.details = details;
    }

    // Getters
    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDetails() {
        return details;
    }
}
