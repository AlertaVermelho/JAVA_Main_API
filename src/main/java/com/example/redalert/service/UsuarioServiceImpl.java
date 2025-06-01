package com.example.redalert.service;

import com.example.redalert.dto.JwtResponseDTO;
import com.example.redalert.dto.UsuarioAtualizacaoDTO;
import com.example.redalert.dto.UsuarioLoginDTO;
import com.example.redalert.dto.UsuarioRegistroDTO;
import com.example.redalert.dto.UsuarioResponseDTO;
import com.example.redalert.exception.EmailJaCadastradoException;
import com.example.redalert.exception.SenhaIncorretaException;
import com.example.redalert.exception.UsuarioNaoEncontradoException;
import com.example.redalert.model.Usuario;
import com.example.redalert.repository.UsuarioRepository;
import com.example.redalert.security.jwt.JwtTokenProvider; // Você precisará criar esta classe

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

// Visibilidade package-private (sem 'public' explícito)
@Service
class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager; // Para o processo de login
    private final JwtTokenProvider jwtTokenProvider;         // Para gerar o token JWT

    // Formatter para datas nas DTOs de resposta
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;


    @Autowired
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
    public UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new EmailJaCadastradoException("O email '" + dto.getEmail() + "' já está cadastrado.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.getNome());
        novoUsuario.setEmail(dto.getEmail());
        novoUsuario.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        // dataCriacao e dataAtualizacao serão gerenciadas pelo @CreationTimestamp/@UpdateTimestamp

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        return new UsuarioResponseDTO(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                ISO_FORMATTER.format(usuarioSalvo.getDataCriacao()),
                ISO_FORMATTER.format(usuarioSalvo.getDataAtualizacao())
        );
    }

    @Override
    public JwtResponseDTO loginUsuario(UsuarioLoginDTO dto) {
        // O AuthenticationManager fará a validação da senha usando o UserDetailsService e PasswordEncoder configurados
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        // Após autenticação bem-sucedida, buscar o usuário para retornar seus dados
        // O 'principal' aqui será o UserDetails carregado pelo seu UserDetailsServiceImpl
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
            // Isso não deveria acontecer se o endpoint estiver protegido corretamente
            throw new UsuarioNaoEncontradoException("Nenhum usuário autenticado encontrado.");
        }
        Usuario usuario = usuarioRepository.findByEmail(userPrincipal.getUsername())
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário autenticado '" + userPrincipal.getUsername() + "' não encontrado no banco de dados."));

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
    public UsuarioResponseDTO atualizarUsuario(Long userId, UsuarioAtualizacaoDTO dto) {
        Usuario usuario = buscarUsuarioPorId(userId); // Reusa o método para buscar e lançar exceção se não encontrar

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
            // dataAtualizacao será atualizada automaticamente pelo @UpdateTimestamp
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
            usuarioRepository.save(usuario); // dataAtualizacao será atualizada
        } else if ((notificationToken == null || notificationToken.isBlank()) && usuario.getTokenNotificacaoPush() != null) {
            // Se um token nulo/vazio for enviado e o usuário tinha um token, remove o token.
            usuario.setTokenNotificacaoPush(null);
            usuarioRepository.save(usuario);
        }
        // Se o token for o mesmo, não faz nada.
    }
}
