package com.example.redalert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para registro de um novo usuário no sistema.")
public class UsuarioRegistroDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres.")
    @Schema(description = "Nome completo do usuário.", example = "João da Silva", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Formato de email inválido.")
    @Size(max = 100, message = "O email não pode exceder 100 caracteres.")
    @Schema(description = "Endereço de e-mail único do usuário.", example = "joao.silva@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres.")
    @Schema(description = "Senha de acesso do usuário. Deve atender aos critérios de força definidos.", 
            example = "Senha@123", 
            minLength = 8, 
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String senha;
}
