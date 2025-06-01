package com.example.redalert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para atualização dos dados de um usuário autenticado. Todos os campos são opcionais, exceto 'senhaAtual' se 'novaSenha' for fornecida.")
public class UsuarioAtualizacaoDTO {

    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres.")
    @Schema(description = "Novo nome completo do usuário (opcional).", example = "João Pereira da Silva", nullable = true)
    private String nome;

    @Email(message = "Formato de email inválido.")
    @Size(max = 100, message = "O email não pode exceder 100 caracteres.")
    @Schema(description = "Novo endereço de e-mail do usuário (opcional, deve ser único se alterado).", example = "joao.p.silva@example.com", nullable = true)
    private String email;

    @Schema(description = "Senha atual do usuário. Obrigatória se 'novaSenha' for fornecida para verificação.", example = "Senha@123", nullable = true)
    private String senhaAtual;

    @Size(min = 8, max = 100, message = "A nova senha deve ter entre 8 e 100 caracteres.")
    @Schema(description = "Nova senha de acesso do usuário (opcional). Se fornecida, 'senhaAtual' é obrigatória.", 
            example = "NovaSenha@456", 
            minLength = 8, 
            maxLength = 100,
            nullable = true)
    private String novaSenha;
}
