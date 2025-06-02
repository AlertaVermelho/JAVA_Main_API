package com.example.redalert.exception.users;

public class JwtSecretNaoConfiguradoException extends RuntimeException {
    public JwtSecretNaoConfiguradoException(String message) {
        super(message);
    }
}
