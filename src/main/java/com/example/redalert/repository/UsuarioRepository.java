package com.example.redalert.repository;

import com.example.redalert.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Opcional para interfaces que estendem JpaRepository, mas boa prática
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Boolean existsByEmail(String email);
    
    // Se você tivesse um campo 'username' único e quisesse buscar por ele:
    // Optional<Usuario> findByUsername(String username);
    // Boolean existsByUsername(String username);
}