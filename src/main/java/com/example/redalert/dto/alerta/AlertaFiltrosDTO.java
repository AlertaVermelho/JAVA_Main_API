package com.example.redalert.dto.alerta;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Data
@Schema(description = "DTO para encapsular os filtros da listagem de alertas.")
public class AlertaFiltrosDTO {

    @Schema(description = "Filtrar por status do alerta.", example = "CLASSIFICADO_IA", nullable = true)
    private String statusAlerta;

    @Schema(description = "Filtrar por severidade classificada pela IA.", example = "ALTA", nullable = true)
    private String severidadeIA;

    @Schema(description = "Filtrar por tipo classificado pela IA.", example = "ALAGAMENTO", nullable = true)
    private String tipoIA;

    @Schema(description = "Data inicial para o filtro de período do reporte (formato ISO 8601 DateTime).", example = "2025-06-01T00:00:00Z", nullable = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant dataInicial;

    @Schema(description = "Data final para o filtro de período do reporte (formato ISO 8601 DateTime).", example = "2025-06-02T23:59:59Z", nullable = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant dataFinal;

    @Schema(description = "Latitude mínima para o bounding box.", example = "-23.60", nullable = true)
    private Double minLat;

    @Schema(description = "Latitude máxima para o bounding box.", example = "-23.50", nullable = true)
    private Double maxLat;

    @Schema(description = "Longitude mínima para o bounding box.", example = "-46.70", nullable = true)
    private Double minLon;

    @Schema(description = "Longitude máxima para o bounding box.", example = "-46.60", nullable = true)
    private Double maxLon;
}
