package com.example.redalert.dto.alerta;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para registrar um novo alerta de usuário.")
public class AlertaRequestDTO {

    @NotBlank(message = "A descrição do texto é obrigatória.")
    @Size(min = 10, max = 2000, message = "A descrição deve ter entre 10 e 2000 caracteres.")
    @Schema(description = "Descrição textual da ocorrência.", 
            example = "Grande alagamento na Rua das Palmeiras, água cobrindo carros.", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String descricaoTexto;

    @NotNull(message = "A latitude é obrigatória.")
    @Schema(description = "Latitude da ocorrência.", example = "-23.550520", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double latitude;

    @NotNull(message = "A longitude é obrigatória.")
    @Schema(description = "Longitude da ocorrência.", example = "-46.633308", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double longitude;

    @NotBlank(message = "O timestamp do cliente é obrigatório.")
    @Schema(description = "Data e hora do reporte no dispositivo do usuário (formato ISO 8601 DateTime String).", 
            example = "2025-06-02T10:30:00Z", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String timestampCliente;
}
