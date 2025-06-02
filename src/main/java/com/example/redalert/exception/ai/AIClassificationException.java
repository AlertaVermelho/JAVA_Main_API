package com.example.redalert.exception.ai;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AIClassificationException extends AIServiceException {
    public AIClassificationException(String message) {
        super(message);
    }

    public AIClassificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
