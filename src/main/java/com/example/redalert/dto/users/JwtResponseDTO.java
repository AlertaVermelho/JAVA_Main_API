package com.example.redalert.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO contendo o token JWT e informações do usuário após login bem-sucedido.")
public class JwtResponseDTO {

    @Schema(description = "Token de acesso JWT.", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2FvLnNpbHZhQGV4YW1wbGUuY29tIiwiaWF0IjoxNzA...")
    private String token;

    @Schema(description = "Tipo do token.", example = "Bearer", defaultValue = "Bearer")
    private String type = "Bearer";

    @Schema(description = "ID do usuário autenticado.", example = "1")
    private Long userId;

    @Schema(description = "Nome do usuário autenticado.", example = "João da Silva")
    private String nome;

    @Schema(description = "Email do usuário autenticado.", example = "joao.silva@example.com")
    private String email;

    public JwtResponseDTO(String token, Long userId, String nome, String email) {
        this.token = token;
        this.userId = userId;
        this.nome = nome;
        this.email = email;
    }
}
