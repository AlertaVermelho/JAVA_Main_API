package com.example.redalert.service.ai;

import com.example.redalert.dto.ai.AIClusteringAlertItemDTO;
import com.example.redalert.dto.ai.AIClusteringRequestDTO;
import com.example.redalert.dto.ai.AIClusteringResponseDTO;
import com.example.redalert.dto.ai.AIClusteringResultItemDTO;
import com.example.redalert.dto.ai.AIHotspotSummaryDTO;
import com.example.redalert.model.AlertaUsuario;
import com.example.redalert.model.HotspotsEventos;
import com.example.redalert.repository.AlertaUsuarioRepository;
import com.example.redalert.repository.HotspotsEventosRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.time.format.DateTimeParseException;

@Service
class HotspotServiceImpl implements IHotspotService {

    private static final Logger logger = LoggerFactory.getLogger(HotspotServiceImpl.class);

    private final AlertaUsuarioRepository alertaUsuarioRepository;
    private final HotspotsEventosRepository hotspotsEventosRepository;
    private final AIServiceClient aiServiceClient;

    // Dependências para a parte de notificação
    // private final CSharpMonitoredLocationsClient csharpMonitoredLocationsClient;
    // private final UsuarioRepository usuarioRepository;
    // private final PushNotificationService pushNotificationService;


    public HotspotServiceImpl(AlertaUsuarioRepository alertaUsuarioRepository,
                              HotspotsEventosRepository hotspotsEventosRepository,
                              AIServiceClient aiServiceClient
                              /*CSharpMonitoredLocationsClient csharpMonitoredLocationsClient,
                              UsuarioRepository usuarioRepository,
                              PushNotificationService pushNotificationService */) {
        this.alertaUsuarioRepository = alertaUsuarioRepository;
        this.hotspotsEventosRepository = hotspotsEventosRepository;
        this.aiServiceClient = aiServiceClient;
        // this.csharpMonitoredLocationsClient = csharpMonitoredLocationsClient;
        // this.usuarioRepository = usuarioRepository;
        // this.pushNotificationService = pushNotificationService;
    }

    @Override
    @Scheduled(fixedDelayString = "${app.hotspot.processing.fixed-delay-ms:300000}", 
               initialDelayString = "${app.hotspot.processing.initial-delay-ms:60000}")
    @Transactional
    public void processarAlertasParaCriacaoDeHotspots() {
        logger.info("INICIANDO JOB: Processamento de alertas para criação/atualização de hotspots...");

        List<AlertaUsuario> alertasParaClusterizar = alertaUsuarioRepository.findByStatusAlertaAndHotspotAssociadoIsNull("CLASSIFICADO_IA");

        if (alertasParaClusterizar == null || alertasParaClusterizar.isEmpty()) {
            logger.info("JOB: Nenhum alerta novo (CLASSIFICADO_IA e sem hotspot) para clusterizar no momento.");
            logger.info("FINALIZANDO JOB: Processamento de hotspots.");
            return;
        }
        logger.info("JOB: Encontrados {} alertas para enviar para clustering.", alertasParaClusterizar.size());

        List<AIClusteringAlertItemDTO> itensParaClusterDTO = alertasParaClusterizar.stream()
                .map(alerta -> new AIClusteringAlertItemDTO(
                        alerta.getId(),
                        alerta.getLatitude().doubleValue(),
                        alerta.getLongitude().doubleValue(),
                        alerta.getSeveridadeIA(),
                        alerta.getTipoIA()
                ))
                .collect(Collectors.toList());
        
        AIClusteringRequestDTO clusteringRequestDTO = new AIClusteringRequestDTO(itensParaClusterDTO);

        AIClusteringResponseDTO clusteringResponse;
        try {
            clusteringResponse = aiServiceClient.clusterAlerts(clusteringRequestDTO);
        } catch (Exception e) {
            logger.error("JOB: Falha ao chamar o serviço de clustering da IA. O processamento de hotspots será abortado para esta execução. Erro: {}", e.getMessage());
            logger.info("FINALIZANDO JOB: Processamento de hotspots com erro na IA.");
            return;
        }

        if (clusteringResponse == null) {
            logger.warn("JOB: Serviço de clustering da IA retornou uma resposta nula.");
            logger.info("FINALIZANDO JOB: Processamento de hotspots.");
            return;
        }
        
        Map<Long, AlertaUsuario> mapaAlertas = alertasParaClusterizar.stream()
                .collect(Collectors.toMap(AlertaUsuario::getId, Function.identity()));

        logger.info("JOB: Marcando hotspots ATIVOS anteriores como OBSOLETOS...");
        Specification<HotspotsEventos> specAtivos = (root, query, cb) -> cb.equal(root.get("statusHotspot"), "ATIVO");
        List<HotspotsEventos> hotspotsAtivosAntigos = hotspotsEventosRepository.findAll(specAtivos);
        
        int contObsoletos = 0;
        if (!hotspotsAtivosAntigos.isEmpty()) {
            for (HotspotsEventos antigo : hotspotsAtivosAntigos) {
                antigo.setStatusHotspot("OBSOLETO");
            }
            hotspotsEventosRepository.saveAllAndFlush(hotspotsAtivosAntigos);
            contObsoletos = hotspotsAtivosAntigos.size();
        }
        logger.info("JOB: {} hotspots antigos marcados como OBSOLETOS.", contObsoletos);

        if (clusteringResponse.getHotspotSummaries() != null) {
            logger.info("JOB: Processando {} sumários de hotspots recebidos da IA.", clusteringResponse.getHotspotSummaries().size());
            for (AIHotspotSummaryDTO summaryDTO : clusteringResponse.getHotspotSummaries()) {
                if (summaryDTO.getClusterLabel() == null || summaryDTO.getClusterLabel() < 0 || summaryDTO.getAlertIdsInCluster() == null || summaryDTO.getAlertIdsInCluster().isEmpty()) {
                    logger.warn("JOB: Sumário de hotspot inválido ou sem alertas recebido da IA (ClusterLabel: {}). Pulando.", summaryDTO.getClusterLabel());
                    continue;
                }

                // Lógica para encontrar/criar/atualizar HotspotsEventos
                // Por enquanto, vamos simplificar: criar um novo hotspot para cada sumário válido recebido.
                // Em uma versão mais avançada, você implementaria uma lógica de "merge" ou atualização
                // se um hotspot similar já existisse (ex: baseado na proximidade do centroide e tipo/severidade).
                
                HotspotsEventos hotspot = new HotspotsEventos();
                hotspot.setCentroideLatitude(BigDecimal.valueOf(summaryDTO.getCentroidLat()));
                hotspot.setCentroideLongitude(BigDecimal.valueOf(summaryDTO.getCentroidLon()));
                hotspot.setNumeroAlertasAgrupados(summaryDTO.getPointCount());
                hotspot.setSeveridadePredominante(summaryDTO.getDominantSeverity());
                hotspot.setTipoPredominante(summaryDTO.getDominantType());
                hotspot.setResumoPublico(summaryDTO.getPublicSummary());
                
                if (summaryDTO.getEstimatedRadiusKm() != null) {
                    hotspot.setRaioEstimadoKm(BigDecimal.valueOf(summaryDTO.getEstimatedRadiusKm()));
                }                

                if (summaryDTO.getLastActivityTimestamp() != null) {
                    try {
                        hotspot.setTimestampUltimaAtividade(Instant.parse(summaryDTO.getLastActivityTimestamp()));
                    } catch (DateTimeParseException e) {
                        logger.warn("JOB: Formato inválido para lastActivityTimestamp do hotspot {}: {}. Usando o momento atual.", summaryDTO.getClusterLabel(), summaryDTO.getLastActivityTimestamp());
                        hotspot.setTimestampUltimaAtividade(Instant.now());
                    }
                } else {
                    hotspot.setTimestampUltimaAtividade(Instant.now());
                }
                hotspot.setStatusHotspot("ATIVO");

                HotspotsEventos hotspotSalvo = hotspotsEventosRepository.save(hotspot);
                logger.info("JOB: Hotspot ID {} (ClusterLabel IA: {}) criado/atualizado.", hotspotSalvo.getId(), summaryDTO.getClusterLabel());

                for (Long alertaId : summaryDTO.getAlertIdsInCluster()) {
                    AlertaUsuario alerta = mapaAlertas.get(alertaId);
                    if (alerta != null) {
                        alerta.setHotspotAssociado(hotspotSalvo);
                        alerta.setStatusAlerta("PROCESSADO_CLUSTER");
                        alertaUsuarioRepository.save(alerta);
                    } else {
                        logger.warn("JOB: Alerta com ID {} listado no sumário do hotspot {}, mas não encontrado na lista original.", alertaId, summaryDTO.getClusterLabel());
                    }
                }
            }
        } else {
            logger.info("JOB: Nenhum sumário de hotspot recebido da IA.");
        }

        if (clusteringResponse.getClusteringResults() != null) {
            for (AIClusteringResultItemDTO resultItem : clusteringResponse.getClusteringResults()) {
                AlertaUsuario alerta = mapaAlertas.get(resultItem.getAlertId());
                if (alerta != null && alerta.getHotspotAssociado() == null) {
                    if (resultItem.getClusterLabel() < 0) {
                        alerta.setStatusAlerta("PROCESSADO_CLUSTER_RUIDO");
                        logger.info("JOB: Alerta ID {} marcado como RUÍDO pelo clustering.", alerta.getId());
                    } else {
                        logger.warn("JOB: Alerta ID {} tem clusterLabel {} mas não estava em hotspotSummaries e não é ruído.", alerta.getId(), resultItem.getClusterLabel());
                        alerta.setStatusAlerta("PROCESSADO_CLUSTER_SEM_HOTSPOT"); 
                    }
                    alertaUsuarioRepository.save(alerta);
                }
            }
        }
        
        // LÓGICA DE NOTIFICAÇÃO PARA LOCAIS MONITORADOS
        // 1. Buscar hotspots ATIVOS que foram recém-criados/atualizados.
        // 2. Chamar csharpMonitoredLocationsClient.getAllActiveMonitoredLocations().
        // 3. Lógica de proximidade.
        // 4. Enviar notificações push.

        logger.info("FINALIZANDO JOB: Processamento de hotspots.");
    }
}
