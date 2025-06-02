package com.example.redalert.service.alerta;

import com.example.redalert.dto.alerta.AlertaRequestDTO;
import com.example.redalert.dto.alerta.AlertaResponseDTO;
import com.example.redalert.dto.alerta.AlertaFiltrosDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAlertaUsuarioService {

    AlertaResponseDTO criarAlerta(AlertaRequestDTO alertaRequestDTO, Long userId);
    Page<AlertaResponseDTO> listarAlertas(AlertaFiltrosDTO filtros, Pageable pageable);
}
