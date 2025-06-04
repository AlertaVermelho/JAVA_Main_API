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
public class HotspotMapInfoDTO {
    @Schema(description = "ID do hotspot.", example = "25")
    private Long hotspotId;

    @Schema(description = "Latitude do centroide do hotspot.", example = "-23.5600")
    private Double centroidLat;

    @Schema(description = "Longitude do centroide do hotspot.", example = "-46.6750")
    private Double centroidLon;

    @Schema(description = "Raio estimado do hotspot em KM.", example = "0.5", nullable = true)
    private Double estimatedRadiusKm;

    @Schema(description = "Severidade predominante no hotspot.", example = "ALTA")
    private String dominantSeverity;

    @Schema(description = "Tipo de evento predominante no hotspot.", example = "ALAGAMENTO")
    private String dominantType;

    @Schema(description = "Resumo público do hotspot para exibição.", example = "Concentração de alagamentos de alta severidade reportados.")
    private String publicSummary;

    @Schema(description = "Número de alertas agrupados neste hotspot.", example = "8")
    private Integer pointCount;

    @Schema(description = "Timestamp da última atividade/alerta reportado no hotspot (formato ISO 8601).", example = "2025-06-03T10:30:00Z")
    private String lastActivityTimestamp;
}
