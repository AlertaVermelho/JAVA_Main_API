package com.example.redalert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representando os dados de um usuário para respostas da API.")
public class UsuarioResponseDTO {

    @Schema(description = "ID único do usuário.", example = "1")
    private Long id;

    @Schema(description = "Nome completo do usuário.", example = "João da Silva")
    private String nome;

    @Schema(description = "Endereço de e-mail do usuário.", example = "joao.silva@example.com")
    private String email;

    @Schema(description = "Data e hora de criação do cadastro do usuário (formato ISO 8601).", example = "2025-06-01T20:30:00.123Z")
    private String dataCriacao;

    // Para GET /users/me, você também incluiria dataAtualizacao
    @Schema(description = "Data e hora da última atualização do cadastro do usuário (formato ISO 8601).", example = "2025-06-01T20:35:00.456Z", nullable = true)
    private String dataAtualizacao;
}