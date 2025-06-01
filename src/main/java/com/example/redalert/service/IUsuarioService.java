package com.example.redalert.service;

import com.example.redalert.dto.JwtResponseDTO;
import com.example.redalert.dto.UsuarioAtualizacaoDTO;
import com.example.redalert.dto.UsuarioLoginDTO;
import com.example.redalert.dto.UsuarioRegistroDTO;
import com.example.redalert.dto.UsuarioResponseDTO;
import com.example.redalert.model.Usuario;

public interface IUsuarioService {

    /**
     * Registra um novo usuário no sistema.
     * @param usuarioRegistroDTO Dados para registro.
     * @return UsuarioResponseDTO com os dados do usuário criado.
     * @throws com.example.redalert.exception.EmailJaCadastradoException Se o email já existir.
     */
    UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO usuarioRegistroDTO);

    /**
     * Autentica um usuário e retorna um token JWT.
     * @param usuarioLoginDTO Dados para login.
     * @return JwtResponseDTO contendo o token e informações do usuário.
     * @throws org.springframework.security.core.AuthenticationException Se a autenticação falhar.
     */
    JwtResponseDTO loginUsuario(UsuarioLoginDTO usuarioLoginDTO);

    /**
     * Busca o usuário autenticado atualmente.
     * @param userPrincipal O principal do usuário autenticado (geralmente obtido do SecurityContext).
     * @return UsuarioResponseDTO com os dados do usuário.
     * @throws com.example.redalert.exception.UsuarioNaoEncontradoException Se o usuário não for encontrado.
     */
    UsuarioResponseDTO getUsuarioAutenticado(org.springframework.security.core.userdetails.UserDetails userPrincipal);
    
    /**
     * Busca um usuário pelo seu ID. (Método interno, pode não ser exposto diretamente na controller)
     * @param id ID do usuário.
     * @return Entidade Usuario.
     */
    Usuario buscarUsuarioPorId(Long id);

    /**
     * Atualiza os dados do usuário autenticado.
     * @param userId ID do usuário a ser atualizado (obtido do token).
     * @param usuarioAtualizacaoDTO Dados para atualização.
     * @return UsuarioResponseDTO com os dados atualizados.
     * @throws com.example.redalert.exception.UsuarioNaoEncontradoException Se o usuário não for encontrado.
     * @throws com.example.redalert.exception.SenhaIncorretaException Se a senha atual estiver incorreta (ao tentar mudar senha).
     */
    UsuarioResponseDTO atualizarUsuario(Long userId, UsuarioAtualizacaoDTO usuarioAtualizacaoDTO);

    /**
     * Atualiza o token de notificação push para o usuário autenticado.
     * @param userId ID do usuário (obtido do token).
     * @param notificationToken O novo token de notificação.
     */
    void atualizarTokenNotificacao(Long userId, String notificationToken);
}
