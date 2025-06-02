package com.example.redalert.dto.alerta;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO representando um alerta de usuário para respostas da API.")
public class AlertaResponseDTO {

    @Schema(description = "ID único do alerta.", example = "101")
    private Long alertId;

    @Schema(description = "ID do usuário que reportou o alerta.", example = "1")
    private Long idUsuarioReportou;

    @Schema(description = "Descrição textual da ocorrência.", example = "Grande alagamento na Rua das Palmeiras.")
    private String descricaoTexto;

    @Schema(description = "Latitude da ocorrência.", example = "-23.550520")
    private Double latitude;

    @Schema(description = "Longitude da ocorrência.", example = "-46.633308")
    private Double longitude;

    @Schema(description = "Data e hora do reporte do alerta (formato ISO 8601).", example = "2025-06-02T10:30:00Z")
    private String timestampReporte; // Formatado como String ISO 8601

    @Schema(description = "Status atual do processamento do alerta.", example = "CLASSIFICADO_IA")
    private String statusAlerta;

    @Schema(description = "Severidade do alerta classificada pela IA.", example = "ALTA", nullable = true)
    private String severidadeIA;

    @Schema(description = "Tipo do alerta classificado pela IA.", example = "ALAGAMENTO", nullable = true)
    private String tipoIA;

    @Schema(description = "ID do hotspot ao qual este alerta está associado (se aplicável).", example = "25", nullable = true)
    private Long idHotspotAssociado;
}
