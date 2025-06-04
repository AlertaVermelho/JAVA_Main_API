package com.example.redalert.dto.mapdata;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryAreaDTO {
    @Schema(description = "Latitude do centro da área consultada.", example = "-23.5505")
    private Double centerLat;

    @Schema(description = "Longitude do centro da área consultada.", example = "-46.6333")
    private Double centerLon;

    @Schema(description = "Raio em KM da área consultada.", example = "10.0")
    private Double radiusKm;
}
