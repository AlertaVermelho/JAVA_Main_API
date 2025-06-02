package com.example.redalert.dto.users;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) 
@Schema(description = "DTO padrão para respostas de erro da API.")
public class ErrorResponseDTO {

    @Schema(description = "Timestamp de quando o erro ocorreu (formato ISO 8601).", example = "2025-06-01T21:10:00.123Z", requiredMode = Schema.RequiredMode.REQUIRED)
    private Instant timestamp;

    @Schema(description = "Código de status HTTP.", example = "400", requiredMode = Schema.RequiredMode.REQUIRED)
    private int status;

    @Schema(description = "Frase curta descrevendo o status HTTP.", example = "Bad Request", requiredMode = Schema.RequiredMode.REQUIRED)
    private String error;

    @Schema(description = "Mensagem detalhada sobre o erro.", example = "Erro de validação nos dados de entrada.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    @Schema(description = "Caminho da URI que originou o erro.", example = "/auth/register", requiredMode = Schema.RequiredMode.REQUIRED)
    private String path;
    
    private List<Map<String, String>> errors;

    public ErrorResponseDTO(Instant timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ErrorResponseDTO(Instant timestamp, int status, String error, String message, String path, List<Map<String, String>> errors) {
        this(timestamp, status, error, message, path);
        this.errors = errors;
    }
}
