package com.example.redalert.service.users;

import com.example.redalert.dto.users.JwtResponseDTO;
import com.example.redalert.dto.users.UsuarioAtualizacaoDTO;
import com.example.redalert.dto.users.UsuarioLoginDTO;
import com.example.redalert.dto.users.UsuarioRegistroDTO;
import com.example.redalert.dto.users.UsuarioRegistroResponseDTO;
import com.example.redalert.dto.users.UsuarioResponseDTO;
import com.example.redalert.model.Usuario;

public interface IUsuarioService {

    UsuarioRegistroResponseDTO registrarUsuario(UsuarioRegistroDTO usuarioRegistroDTO); 
    JwtResponseDTO loginUsuario(UsuarioLoginDTO usuarioLoginDTO);
    UsuarioResponseDTO getUsuarioAutenticado(org.springframework.security.core.userdetails.UserDetails userPrincipal); 
    Usuario buscarUsuarioPorId(Long id);
    UsuarioResponseDTO atualizarUsuario(Long userId, UsuarioAtualizacaoDTO usuarioAtualizacaoDTO);
    void atualizarTokenNotificacao(Long userId, String notificationToken);
}
