package com.example.redalert.service.map;

import com.example.redalert.dto.alerta.AlertaFiltrosDTO;
import com.example.redalert.dto.alerta.AlertaResponseDTO;
import com.example.redalert.dto.mapdata.AlertaMapInfoDTO;
import com.example.redalert.dto.mapdata.HotspotMapInfoDTO;
import com.example.redalert.dto.mapdata.MapDataResponseDTO;
import com.example.redalert.dto.mapdata.QueryAreaDTO;
import com.example.redalert.model.HotspotsEventos;
import com.example.redalert.repository.HotspotsEventosRepository;
import com.example.redalert.service.alerta.IAlertaUsuarioService;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
class MapDataServiceImpl implements IMapDataService {

    private static final Logger logger = LoggerFactory.getLogger(MapDataServiceImpl.class);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;
    private static final double DEGREES_TO_KM = 111.32;

    private final HotspotsEventosRepository hotspotsEventosRepository;
    private final IAlertaUsuarioService alertaUsuarioService;

    public MapDataServiceImpl(HotspotsEventosRepository hotspotsEventosRepository,
                              IAlertaUsuarioService alertaUsuarioService) {
        this.hotspotsEventosRepository = hotspotsEventosRepository;
        this.alertaUsuarioService = alertaUsuarioService;
    }

    @Override
    @Transactional(readOnly = true)
    public MapDataResponseDTO getMapData(double currentLat, double currentLon, double radiusKm, Integer zoomLevel) {
        logger.info("Buscando dados do mapa para Lat: {}, Lon: {}, Raio: {}km, Zoom: {}", currentLat, currentLon, radiusKm, zoomLevel);

        QueryAreaDTO queryArea = new QueryAreaDTO(currentLat, currentLon, radiusKm);

        List<HotspotMapInfoDTO> activeHotspots = findActiveHotspotsInArea(currentLat, currentLon, radiusKm);

        double latDelta = radiusKm / DEGREES_TO_KM;
        double lonDelta = radiusKm / (DEGREES_TO_KM * Math.cos(Math.toRadians(currentLat)));

        AlertaFiltrosDTO alertFilters = new AlertaFiltrosDTO();
        alertFilters.setMinLat(currentLat - latDelta);
        alertFilters.setMaxLat(currentLat + latDelta);
        alertFilters.setMinLon(currentLon - lonDelta);
        alertFilters.setMaxLon(currentLon + lonDelta);

        Pageable alertsPageable = PageRequest.of(0, 50); 
        
        Page<AlertaResponseDTO> paginaAlertasIndividuais = alertaUsuarioService.listarAlertas(alertFilters, alertsPageable);
        
        List<AlertaMapInfoDTO> publicAlerts = paginaAlertasIndividuais.getContent().stream()
            .filter(alertaDTO -> {
                return true; 
            })
            .map(this::mapToAlertaMapInfoDTO)
            .collect(Collectors.toList());

        return new MapDataResponseDTO(queryArea, activeHotspots, publicAlerts);
    }

    private List<HotspotMapInfoDTO> findActiveHotspotsInArea(double centerLat, double centerLon, double radiusKm) {
        List<HotspotsEventos> allActiveHotspots = hotspotsEventosRepository.findAll(
            (root, query, cb) -> cb.equal(root.get("statusHotspot"), "ATIVO")
        );

        return allActiveHotspots.stream()
            .filter(hotspot -> {
                if (hotspot.getCentroideLatitude() == null || hotspot.getCentroideLongitude() == null) return false;
                double distancia = calcularDistanciaKm(
                        centerLat, centerLon,
                        hotspot.getCentroideLatitude().doubleValue(),
                        hotspot.getCentroideLongitude().doubleValue()
                );
                double raioHotspot = (hotspot.getRaioEstimadoKm() != null) ? hotspot.getRaioEstimadoKm().doubleValue() : 0.5; 
                return distancia <= (radiusKm + raioHotspot);
            })
            .map(this::mapToHotspotMapInfoDTO)
            .collect(Collectors.toList());
    }

    private double calcularDistanciaKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; 
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private HotspotMapInfoDTO mapToHotspotMapInfoDTO(HotspotsEventos hotspot) {
        if (hotspot == null) return null;
        return new HotspotMapInfoDTO(
                hotspot.getId(),
                hotspot.getCentroideLatitude() != null ? hotspot.getCentroideLatitude().doubleValue() : null,
                hotspot.getCentroideLongitude() != null ? hotspot.getCentroideLongitude().doubleValue() : null,
                hotspot.getRaioEstimadoKm() != null ? hotspot.getRaioEstimadoKm().doubleValue() : null,
                hotspot.getSeveridadePredominante(),
                hotspot.getTipoPredominante(),
                hotspot.getResumoPublico(),
                hotspot.getNumeroAlertasAgrupados(),
                hotspot.getTimestampUltimaAtividade() != null ? ISO_FORMATTER.format(hotspot.getTimestampUltimaAtividade()) : null
        );
    }

    private AlertaMapInfoDTO mapToAlertaMapInfoDTO(AlertaResponseDTO alertaDTO) {
        if (alertaDTO == null) return null;
        String briefDescription = alertaDTO.getDescricaoTexto();
        if (briefDescription != null && briefDescription.length() > 100) { 
            briefDescription = briefDescription.substring(0, 97) + "...";
        }
        return new AlertaMapInfoDTO(
                alertaDTO.getAlertId(),
                alertaDTO.getLatitude(),
                alertaDTO.getLongitude(),
                alertaDTO.getSeveridadeIA(),
                alertaDTO.getTipoIA(),
                briefDescription, 
                alertaDTO.getTimestampReporte()
        );
    }
}
