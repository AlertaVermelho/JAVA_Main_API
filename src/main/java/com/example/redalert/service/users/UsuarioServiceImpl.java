package com.example.redalert.service.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.redalert.dto.users.JwtResponseDTO;
import com.example.redalert.dto.users.UsuarioAtualizacaoDTO;
import com.example.redalert.dto.users.UsuarioLoginDTO;
import com.example.redalert.dto.users.UsuarioRegistroDTO;
import com.example.redalert.dto.users.UsuarioRegistroResponseDTO;
import com.example.redalert.dto.users.UsuarioResponseDTO;
import com.example.redalert.exception.users.EmailJaCadastradoException;
import com.example.redalert.exception.users.SenhaIncorretaException;
import com.example.redalert.exception.users.UsuarioNaoEncontradoException;
import com.example.redalert.model.Usuario;
import com.example.redalert.repository.UsuarioRepository;
import com.example.redalert.security.jwt.JwtTokenProvider;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder,
                              AuthenticationManager authenticationManager,
                              JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public UsuarioRegistroResponseDTO registrarUsuario(UsuarioRegistroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new EmailJaCadastradoException("O email '" + dto.getEmail() + "' já está cadastrado.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.getNome());
        novoUsuario.setEmail(dto.getEmail());
        novoUsuario.setSenhaHash(passwordEncoder.encode(dto.getSenha()));

        Usuario usuarioSalvo = usuarioRepository.saveAndFlush(novoUsuario);

        String dataCriacaoFormatada = null;
        if (usuarioSalvo.getDataCriacao() != null) {
            dataCriacaoFormatada = ISO_FORMATTER.format(usuarioSalvo.getDataCriacao());
        } else {
            logger.warn("dataCriacao é nula para o usuário recém-salvo ID: {}", usuarioSalvo.getId());
        }

        return new UsuarioRegistroResponseDTO(
            usuarioSalvo.getId(),
            usuarioSalvo.getNome(),
            usuarioSalvo.getEmail(),
            dataCriacaoFormatada        
        );
    }

    @Override
    public JwtResponseDTO loginUsuario(UsuarioLoginDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado após login bem-sucedido: " + userDetails.getUsername()));

        return new JwtResponseDTO(jwt, usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário com ID " + id + " não encontrado."));
    }


    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO getUsuarioAutenticado(UserDetails userPrincipal) {
        if (userPrincipal == null) {
            throw new UsuarioNaoEncontradoException("Nenhum usuário autenticado encontrado.");
        }
        Usuario usuario = usuarioRepository.findByEmail(userPrincipal.getUsername())
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário autenticado '" + userPrincipal.getUsername() + "' não encontrado no banco de dados."));

        String dataCriacaoFormatada = usuario.getDataCriacao() != null ? ISO_FORMATTER.format(usuario.getDataCriacao()) : null;
        String dataAtualizacaoFormatada = usuario.getDataAtualizacao() != null ? ISO_FORMATTER.format(usuario.getDataAtualizacao()) : null;

        return new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            dataCriacaoFormatada,
            dataAtualizacaoFormatada
        );
    }

    @Override
    @Transactional
    public UsuarioResponseDTO atualizarUsuario(Long userId, UsuarioAtualizacaoDTO dto) {

        Usuario usuario = buscarUsuarioPorId(userId);        
        boolean modificado = false;

        if (dto.getNome() != null && !dto.getNome().isBlank() && !dto.getNome().equals(usuario.getNome())) {
            usuario.setNome(dto.getNome());
            modificado = true;
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().equalsIgnoreCase(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new EmailJaCadastradoException("O novo email '" + dto.getEmail() + "' já está em uso por outro usuário.");
            }
            usuario.setEmail(dto.getEmail());
            modificado = true;
        }

        if (dto.getNovaSenha() != null && !dto.getNovaSenha().isBlank()) {
            if (dto.getSenhaAtual() == null || dto.getSenhaAtual().isBlank()) {
                throw new IllegalArgumentException("A senha atual é obrigatória para definir uma nova senha.");
            }
            if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenhaHash())) {
                throw new SenhaIncorretaException("A senha atual fornecida está incorreta.");
            }
            usuario.setSenhaHash(passwordEncoder.encode(dto.getNovaSenha()));
            modificado = true;
        }

        if (modificado) {
            usuario = usuarioRepository.save(usuario);
        }

        return new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            ISO_FORMATTER.format(usuario.getDataCriacao()),
            ISO_FORMATTER.format(usuario.getDataAtualizacao())
        );
    }

    @Override
    @Transactional
    public void atualizarTokenNotificacao(Long userId, String notificationToken) {
        Usuario usuario = buscarUsuarioPorId(userId);
        
        if (notificationToken != null && !notificationToken.isBlank() && 
            (usuario.getTokenNotificacaoPush() == null || !usuario.getTokenNotificacaoPush().equals(notificationToken))) {
            usuario.setTokenNotificacaoPush(notificationToken);
            usuarioRepository.save(usuario);
        } else if ((notificationToken == null || notificationToken.isBlank()) && usuario.getTokenNotificacaoPush() != null) {
            usuario.setTokenNotificacaoPush(null);
            usuarioRepository.save(usuario);
        }
    }

    @Override
    @Transactional
    public void deletarMinhaContaHard(UserDetails userPrincipal) {
        if (userPrincipal == null || userPrincipal.getUsername() == null) {
            throw new IllegalStateException("UserDetails ou username não pode ser nulo para deletar conta.");
        }
        String emailUsuario = userPrincipal.getUsername();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário autenticado '" + emailUsuario + "' não encontrado para exclusão."));

        try {
            // Regra de negócio só excluir de fato o usuario se ele não tiver alertas associados
            usuarioRepository.deleteById(usuario.getId());
            logger.info("Usuário ID {} (Email: {}) excluído permanentemente (hard delete).", usuario.getId(), emailUsuario);
        } catch (DataIntegrityViolationException e) {
            logger.warn("Falha ao tentar excluir usuário ID {} (Email: {}). Provavelmente devido a alertas existentes. Erro: {}", 
                        usuario.getId(), emailUsuario, e.getMessage());
            throw new DataIntegrityViolationException(
                "Não foi possível excluir o usuário. Verifique se existem alertas associados a esta conta.", e);
        }
    }
}
