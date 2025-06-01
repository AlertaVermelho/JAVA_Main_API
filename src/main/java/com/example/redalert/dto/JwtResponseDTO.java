package com.example.redalert.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDTO {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String nome;
    private String email;

    public JwtResponseDTO(String token, Long userId, String nome, String email) {
        this.token = token;
        this.userId = userId;
        this.nome = nome;
        this.email = email;
    }
}
