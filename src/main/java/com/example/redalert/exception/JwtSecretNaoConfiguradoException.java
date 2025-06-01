package com.example.redalert.exception;

public class JwtSecretNaoConfiguradoException extends RuntimeException {
    public JwtSecretNaoConfiguradoException(String message) {
        super(message);
    }
}
