package com.example.redalert.controller.alerta;

import com.example.redalert.dto.alerta.AlertaFiltrosDTO;
import com.example.redalert.dto.alerta.AlertaRequestDTO;
import com.example.redalert.dto.alerta.AlertaResponseDTO;
import com.example.redalert.dto.users.UsuarioResponseDTO;
import com.example.redalert.service.alerta.IAlertaUsuarioService;
import com.example.redalert.service.users.IUsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/alerts")
@Tag(name = "Alertas", description = "Endpoints para gerenciamento de alertas de usuários")
public class AlertaController {

    private final IAlertaUsuarioService alertaUsuarioService;
    private final IUsuarioService usuarioService;

    public AlertaController(IAlertaUsuarioService alertaUsuarioService, IUsuarioService usuarioService) {
        this.alertaUsuarioService = alertaUsuarioService;
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Registra um novo alerta de usuário",
               description = "Cria um novo alerta reportado por um usuário autenticado e dispara o processo de classificação pela IA. Contrato 2.1.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<AlertaResponseDTO> criarAlerta(
            @Valid @RequestBody AlertaRequestDTO alertaRequestDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        
        UsuarioResponseDTO usuarioLogado = usuarioService.getUsuarioAutenticado(userDetails);
        Long userId = usuarioLogado.getId();

        AlertaResponseDTO novoAlerta = alertaUsuarioService.criarAlerta(alertaRequestDTO, userId);
        
        return new ResponseEntity<>(novoAlerta, HttpStatus.CREATED);
    }

    @Operation(summary = "Lista alertas com filtros e paginação (uso interno/específico)",
               description = "Retorna uma lista paginada de alertas com base nos filtros fornecidos. Contrato 2.2. " + 
                             "Este endpoint é primariamente para uso interno do sistema (ex: alimentar o job de clustering ou o endpoint /mapdata).")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<Page<AlertaResponseDTO>> listarAlertas(
            @Parameter(description = "Filtros para a busca de alertas. Todos os campos são opcionais.") AlertaFiltrosDTO filtros,
            @PageableDefault(
                size = 10, 
                sort = "timestampReporte",
                direction = Sort.Direction.DESC
            )
            @Parameter(description = "Parâmetros de paginação (page, size, sort). Ex: page=0&size=20&sort=timestampReporte,desc") Pageable pageable) 
        {
        
        Page<AlertaResponseDTO> paginaAlertas = alertaUsuarioService.listarAlertas(filtros, pageable);
        return ResponseEntity.ok(paginaAlertas);
    }
}
