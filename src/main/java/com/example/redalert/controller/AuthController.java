package com.example.redalert.controller;

import com.example.redalert.dto.JwtResponseDTO;
import com.example.redalert.dto.UsuarioLoginDTO;
import com.example.redalert.dto.UsuarioRegistroDTO;
import com.example.redalert.dto.UsuarioRegistroResponseDTO;
import com.example.redalert.service.IUsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints para registro e login de usuários")
public class AuthController {

    private final IUsuarioService usuarioService;

    public AuthController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Registra um novo usuário no sistema",
               description = "Cria um novo usuário com nome, email e senha. O email deve ser único.")
    @PostMapping("/register")
    public ResponseEntity<UsuarioRegistroResponseDTO> registrarUsuario(@Valid @RequestBody UsuarioRegistroDTO usuarioRegistroDTO) {
        UsuarioRegistroResponseDTO novoUsuario = usuarioService.registrarUsuario(usuarioRegistroDTO);
        return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
    }

    @Operation(summary = "Autentica um usuário existente",
               description = "Realiza o login do usuário com email e senha, retornando um token JWT em caso de sucesso.")
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> loginUsuario(@Valid @RequestBody UsuarioLoginDTO usuarioLoginDTO) {
        JwtResponseDTO jwtResponse = usuarioService.loginUsuario(usuarioLoginDTO);
        return ResponseEntity.ok(jwtResponse);
    }
}
