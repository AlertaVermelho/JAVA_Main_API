package com.example.redalert.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "USUARIOS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_usuarios_generator")
    @SequenceGenerator(name = "seq_usuarios_generator", sequenceName = "SEQ_USUARIOS", allocationSize = 1)
    @Column(name = "id_usuario")
    private Long id;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 150, message = "O nome não pode exceder 150 caracteres.")
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Formato de email inválido.")
    @Size(max = 100, message = "O email não pode exceder 100 caracteres.")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Column(name = "token_notificacao_push", length = 255, nullable = true)
    private String tokenNotificacaoPush;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private Instant dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao", nullable = false)
    private Instant dataAtualizacao;
}
