package com.example.redalert.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representando a resposta de sucesso ao registrar um novo usuário.")
public class UsuarioRegistroResponseDTO {

    @Schema(description = "ID único do usuário.", example = "1")
    private Long id;

    @Schema(description = "Nome completo do usuário.", example = "João da Silva")
    private String nome;

    @Schema(description = "Endereço de e-mail do usuário.", example = "joao.silva@example.com")
    private String email;

    @Schema(description = "Data e hora de criação do cadastro do usuário (formato ISO 8601).", example = "2025-06-01T20:30:00.123Z")
    private String dataCriacao;
}
