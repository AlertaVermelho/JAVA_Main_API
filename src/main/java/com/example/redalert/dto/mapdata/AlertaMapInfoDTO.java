package com.example.redalert.dto.mapdata;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlertaMapInfoDTO {
    @Schema(description = "ID do alerta.", example = "101")
    private Long alertId;

    @Schema(description = "Latitude da ocorrência.", example = "-23.550520")
    private Double latitude;

    @Schema(description = "Longitude da ocorrência.", example = "-46.633308")
    private Double longitude;

    @Schema(description = "Severidade do alerta classificada pela IA.", example = "ALTA", nullable = true)
    private String severityIA;

    @Schema(description = "Tipo do alerta classificado pela IA.", example = "ALAGAMENTO", nullable = true)
    private String typeIA;
    
    @Schema(description = "Descrição breve do alerta (pode ser truncada ou um resumo).", example = "Alagamento intenso na Rua das Acácias...", nullable = true)
    private String briefDescription;

    @Schema(description = "Timestamp do reporte do alerta (formato ISO 8601).", example = "2025-06-02T10:00:00Z")
    private String timestampReporte;
}
