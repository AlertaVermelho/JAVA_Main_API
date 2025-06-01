package com.example.redalert.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String dataCriacao; // String formatada ISO 8601
    // Para GET /users/me, você também incluiria dataAtualizacao
    private String dataAtualizacao; // String formatada ISO 8601
}