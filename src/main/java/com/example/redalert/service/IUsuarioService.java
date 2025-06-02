package com.example.redalert.service;

import com.example.redalert.dto.JwtResponseDTO;
import com.example.redalert.dto.UsuarioAtualizacaoDTO;
import com.example.redalert.dto.UsuarioLoginDTO;
import com.example.redalert.dto.UsuarioRegistroDTO;
import com.example.redalert.dto.UsuarioResponseDTO;
import com.example.redalert.dto.UsuarioRegistroResponseDTO;
import com.example.redalert.model.Usuario;

public interface IUsuarioService {

    UsuarioRegistroResponseDTO registrarUsuario(UsuarioRegistroDTO usuarioRegistroDTO); 
    JwtResponseDTO loginUsuario(UsuarioLoginDTO usuarioLoginDTO);
    UsuarioResponseDTO getUsuarioAutenticado(org.springframework.security.core.userdetails.UserDetails userPrincipal); 
    Usuario buscarUsuarioPorId(Long id);
    UsuarioResponseDTO atualizarUsuario(Long userId, UsuarioAtualizacaoDTO usuarioAtualizacaoDTO);
    void atualizarTokenNotificacao(Long userId, String notificationToken);
}
