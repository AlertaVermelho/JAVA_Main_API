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

import java.time.Instant; // Usar Instant para timestamps é uma boa prática com JPA/Hibernate modernos

@Entity
@Table(name = "USUARIOS") // Mapeia para a tabela USUARIOS que definimos
@Data // Anotação do Lombok para gerar getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: construtor sem argumentos
@AllArgsConstructor // Lombok: construtor com todos os argumentos
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_usuarios_generator")
    @SequenceGenerator(name = "seq_usuarios_generator", sequenceName = "SEQ_USUARIOS", allocationSize = 1)
    @Column(name = "id_usuario")
    private Long id; // Mapeia para id_usuario NUMBER PRIMARY KEY

    @NotBlank(message = "O nome é obrigatório.") // Validação para o DTO de registro
    @Size(max = 150, message = "O nome não pode exceder 150 caracteres.")
    @Column(name = "nome", nullable = false, length = 150)
    private String nome; // Mapeia para nome VARCHAR2(150) NOT NULL

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Formato de email inválido.") // Validação de formato
    @Size(max = 100, message = "O email não pode exceder 100 caracteres.")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email; // Mapeia para email VARCHAR2(100) NOT NULL UNIQUE

    // A senha que vem do request (DTO) será 'senha', mas no banco guardamos 'senha_hash'
    // A anotação @NotBlank e @Size para senha deve estar no DTO de registro, não diretamente aqui
    // pois esta entidade representa o que está no banco.
    @Column(name = "senha_hash", nullable = false, length = 255) // Ajuste o tamanho se seu hash for maior
    private String senhaHash; // Mapeia para senha_hash VARCHAR2(255) NOT NULL

    @Column(name = "token_notificacao_push", length = 255, nullable = true)
    private String tokenNotificacaoPush; // Mapeia para token_notificacao_push VARCHAR2(255) NULL

    @CreationTimestamp // Hibernate anotação para gerenciar automaticamente na criação
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private Instant dataCriacao; // Mapeia para data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL

    @UpdateTimestamp // Hibernate anotação para gerenciar automaticamente na atualização
    @Column(name = "data_atualizacao", nullable = false)
    private Instant dataAtualizacao; // Mapeia para data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL

    // Construtores, Getters e Setters são gerados pelo Lombok (@Data, @NoArgsConstructor, @AllArgsConstructor)
    // Se você não usar Lombok, precisará criá-los manualmente.

    // Considerações:
    // O campo 'senha' que vem do request do usuário não é armazenado diretamente.
    // No serviço de usuário, você receberá a senha, fará o hash, e salvará o hash em 'senhaHash'.
}