package com.example.redalert.dto.mapdata;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO contendo os dados agregados para exibição no mapa.")
public class MapDataResponseDTO {
    @Schema(description = "Informações sobre a área geográfica consultada.")
    private QueryAreaDTO queryArea;

    @Schema(description = "Lista de hotspots ativos na área consultada.")
    private List<HotspotMapInfoDTO> hotspots;

    @Schema(description = "Lista de alertas individuais públicos relevantes na área consultada.")
    private List<AlertaMapInfoDTO> publicAlerts;
}
