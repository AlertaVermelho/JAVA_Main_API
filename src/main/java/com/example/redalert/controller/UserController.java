package com.example.redalert.controller;

import com.example.redalert.dto.TokenResponseDTO;
import com.example.redalert.dto.TokenNotificacaoDTO;
import com.example.redalert.dto.UsuarioAtualizacaoDTO;
import com.example.redalert.dto.UsuarioResponseDTO;
import com.example.redalert.service.IUsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuário", description = "Endpoints para gerenciamento de dados do usuário autenticado.")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final IUsuarioService usuarioService;

    public UserController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Obtém o perfil do usuário autenticado.",
               description = "Retorna os dados cadastrais do usuário que está fazendo a requisição (logado), conforme o contrato 1.3.")
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioAutenticado(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        UsuarioResponseDTO usuarioResponse = usuarioService.getUsuarioAutenticado(userDetails);
        return ResponseEntity.ok(usuarioResponse);
    }

    @Operation(summary = "Atualiza o perfil do usuário autenticado.",
               description = "Permite que o usuário autenticado atualize seu nome, email ou senha, conforme o contrato 1.4. Para alterar a senha, a senha atual deve ser fornecida.")
    @PutMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UsuarioAtualizacaoDTO usuarioAtualizacaoDTO) {
        
        UsuarioResponseDTO usuarioLogadoInfo = usuarioService.getUsuarioAutenticado(userDetails);
        Long userId = usuarioLogadoInfo.getId();

        UsuarioResponseDTO usuarioAtualizado = usuarioService.atualizarUsuario(userId, usuarioAtualizacaoDTO);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @Operation(summary = "Atualiza o token de notificação push do usuário autenticado.",
               description = "Registra ou atualiza o token do dispositivo do usuário para o envio de notificações push, conforme o contrato 1.5.")
    @PutMapping("/me/notification-token")
    public ResponseEntity<TokenResponseDTO> atualizarTokenNotificacao(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TokenNotificacaoDTO tokenNotificacaoDTO) {
        
        UsuarioResponseDTO usuarioLogadoInfo = usuarioService.getUsuarioAutenticado(userDetails);
        Long userId = usuarioLogadoInfo.getId();

        TokenResponseDTO responseBody = new TokenResponseDTO("Token de notificação atualizado com sucesso.");

        usuarioService.atualizarTokenNotificacao(userId, tokenNotificacaoDTO.getNotificationToken());
        return ResponseEntity.ok(responseBody);
    }
}
