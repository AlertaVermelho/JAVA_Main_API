package com.example.redalert.service.map;

import com.example.redalert.dto.mapdata.MapDataResponseDTO;
import org.springframework.transaction.annotation.Transactional;

public interface IMapDataService {

    @Transactional(readOnly = true)
    MapDataResponseDTO getMapData(double currentLat, double currentLon, double radiusKm, Integer zoomLevel);
}
