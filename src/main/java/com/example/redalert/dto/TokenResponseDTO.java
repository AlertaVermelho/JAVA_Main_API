package com.example.redalert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para respostas genéricas de mensagem da API.")
public class TokenResponseDTO {

    @Schema(description = "Mensagem de resultado da operação.", example = "Operação realizada com sucesso.")
    private String message;
}
