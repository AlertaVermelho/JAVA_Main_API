package com.example.redalert.controller.map;

import com.example.redalert.dto.mapdata.MapDataResponseDTO;
import com.example.redalert.service.map.IMapDataService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mapdata")
@Tag(name = "Dados do Mapa", description = "Endpoints para obter dados agregados para o mapa principal do AlertaÁgil.")
public class MapController {

    private final IMapDataService mapDataService;

    public MapController(IMapDataService mapDataService) {
        this.mapDataService = mapDataService;
    }

    @Operation(summary = "Busca dados agregados para o mapa",
               description = "Retorna hotspots ativos e alertas públicos relevantes para uma determinada área geográfica, " +
                             "usada para popular o mapa principal do aplicativo móvel. Contrato 4.")
    @GetMapping
    public ResponseEntity<MapDataResponseDTO> getMapData(
            @Parameter(description = "Latitude atual do centro do mapa do usuário.", required = true, example = "-23.550520")
            @RequestParam double currentLat,

            @Parameter(description = "Longitude atual do centro do mapa do usuário.", required = true, example = "-46.633308")
            @RequestParam double currentLon,

            @Parameter(description = "Raio em quilômetros a partir do centro, definindo a área de busca.", required = true, example = "10.0")
            @RequestParam double radiusKm,

            @Parameter(description = "Nível de zoom atual do mapa no app (opcional, para otimizações futuras).", required = false, example = "15")
            @RequestParam(required = false) Integer zoomLevel) {

        MapDataResponseDTO mapData = mapDataService.getMapData(currentLat, currentLon, radiusKm, zoomLevel);
        return ResponseEntity.ok(mapData);
    }
}
