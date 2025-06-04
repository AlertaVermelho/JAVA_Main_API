package com.example.redalert.service.alerta;

import com.example.redalert.service.ai.AIServiceClient;
import com.example.redalert.dto.alerta.AlertaFiltrosDTO;
import com.example.redalert.dto.alerta.AlertaRequestDTO;
import com.example.redalert.dto.alerta.AlertaResponseDTO;
import com.example.redalert.dto.ai.AIClassificationRequestDTO;
import com.example.redalert.dto.ai.AIClassificationResponseDTO;
import com.example.redalert.exception.users.UsuarioNaoEncontradoException;
import com.example.redalert.model.AlertaUsuario;
import com.example.redalert.model.Usuario;
import com.example.redalert.repository.AlertaUsuarioRepository;
import com.example.redalert.repository.UsuarioRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Service
class AlertaUsuarioServiceImpl implements IAlertaUsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(AlertaUsuarioServiceImpl.class);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    private final AlertaUsuarioRepository alertaUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final AIServiceClient aiServiceClient;

    public AlertaUsuarioServiceImpl(AlertaUsuarioRepository alertaUsuarioRepository,
                                    UsuarioRepository usuarioRepository,
                                    AIServiceClient aiServiceClient) {
        this.alertaUsuarioRepository = alertaUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.aiServiceClient = aiServiceClient;
    }

    @Override
    @Transactional
    public AlertaResponseDTO criarAlerta(AlertaRequestDTO dto, Long userId) {
        Usuario usuarioReportou = usuarioRepository.findById(userId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário com ID " + userId + " não encontrado ao tentar reportar alerta."));

        AlertaUsuario novoAlerta = new AlertaUsuario();
        novoAlerta.setUsuarioReportou(usuarioReportou);
        novoAlerta.setDescricaoTexto(dto.getDescricaoTexto());

        if (dto.getLatitude() != null) {
            novoAlerta.setLatitude(BigDecimal.valueOf(dto.getLatitude()));
        }
        if (dto.getLongitude() != null) {
            novoAlerta.setLongitude(BigDecimal.valueOf(dto.getLongitude()));
        }
        
        try {
            novoAlerta.setTimestampReporte(Instant.parse(dto.getTimestampCliente()));
        } catch (DateTimeParseException e) {
            logger.warn("Timestamp do cliente inválido: {}. Usando o timestamp atual do servidor.", dto.getTimestampCliente(), e);
            novoAlerta.setTimestampReporte(Instant.now());
        }
        
        novoAlerta.setStatusAlerta("PENDENTE_IA");

        AlertaUsuario alertaSalvoInicial = alertaUsuarioRepository.saveAndFlush(novoAlerta);
        logger.info("Alerta inicial salvo com ID: {}", alertaSalvoInicial.getId());

        try {
            AIClassificationRequestDTO aiRequest = new AIClassificationRequestDTO(alertaSalvoInicial.getId(), alertaSalvoInicial.getDescricaoTexto());
            AIClassificationResponseDTO aiResponse = aiServiceClient.classifyText(aiRequest);

            if (aiResponse != null && aiResponse.getClassifiedSeverity() != null && aiResponse.getClassifiedType() != null) {
                alertaSalvoInicial.setSeveridadeIA(aiResponse.getClassifiedSeverity());
                alertaSalvoInicial.setTipoIA(aiResponse.getClassifiedType());
                alertaSalvoInicial.setStatusAlerta("CLASSIFICADO_IA");
                alertaSalvoInicial = alertaUsuarioRepository.save(alertaSalvoInicial);
                logger.info("Alerta ID {} classificado pela IA: Tipo={}, Severidade={}", 
                            alertaSalvoInicial.getId(), aiResponse.getClassifiedType(), aiResponse.getClassifiedSeverity());
            } else {
                logger.warn("Falha ao classificar alerta ID {} pela IA ou resposta incompleta. Status permanece PENDENTE_IA.", alertaSalvoInicial.getId());
            }
        } catch (Exception e) {
            logger.error("Erro ao chamar o serviço de IA para o alerta ID {}: {}", alertaSalvoInicial.getId(), e.getMessage());
        }

        return mapToAlertaResponseDTO(alertaSalvoInicial);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlertaResponseDTO> listarAlertas(AlertaFiltrosDTO filtros, Pageable pageable) {
        Specification<AlertaUsuario> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filtros.getStatusAlerta() != null && !filtros.getStatusAlerta().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("statusAlerta"), filtros.getStatusAlerta()));
            }
            if (filtros.getSeveridadeIA() != null && !filtros.getSeveridadeIA().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("severidadeIA"), filtros.getSeveridadeIA()));
            }
            if (filtros.getTipoIA() != null && !filtros.getTipoIA().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("tipoIA"), filtros.getTipoIA()));
            }
            if (filtros.getDataInicial() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestampReporte"), filtros.getDataInicial()));
            }
            if (filtros.getDataFinal() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestampReporte"), filtros.getDataFinal()));
            }
            if (filtros.getMinLat() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("latitude"), filtros.getMinLat()));
            }
            if (filtros.getMaxLat() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("latitude"), filtros.getMaxLat()));
            }
            if (filtros.getMinLon() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("longitude"), filtros.getMinLon()));
            }
            if (filtros.getMaxLon() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("longitude"), filtros.getMaxLon()));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };

        Page<AlertaUsuario> paginaAlertas = alertaUsuarioRepository.findAll(spec, pageable);
        
        return paginaAlertas.map(this::mapToAlertaResponseDTO);
    }

    private AlertaResponseDTO mapToAlertaResponseDTO(AlertaUsuario alerta) {
        if (alerta == null) {
            return null;
        }

        Long idDoHotspot = null;
        if (alerta.getHotspotAssociado() != null) {
            idDoHotspot = alerta.getHotspotAssociado().getId();
        }

        // Double latitudeDto = null;
        // if (alerta.getLatitude() != null) {
        //     latitudeDto = alerta.getLatitude().doubleValue();
        // }

        // Double longitudeDto = null;
        // if (alerta.getLongitude() != null) {
        //     longitudeDto = alerta.getLongitude().doubleValue();
        // }

        Double latitudeDto = (alerta.getLatitude() != null) ? alerta.getLatitude().doubleValue() : null;
        Double longitudeDto = (alerta.getLongitude() != null) ? alerta.getLongitude().doubleValue() : null;

        return new AlertaResponseDTO(
            alerta.getId(),
            alerta.getUsuarioReportou() != null ? alerta.getUsuarioReportou().getId() : null,
            alerta.getDescricaoTexto(),
            latitudeDto,
            longitudeDto,
            alerta.getTimestampReporte() != null ? ISO_FORMATTER.format(alerta.getTimestampReporte()) : null,
            alerta.getStatusAlerta(),
            alerta.getSeveridadeIA(),
            alerta.getTipoIA(),
            idDoHotspot 
        );
    }
}
