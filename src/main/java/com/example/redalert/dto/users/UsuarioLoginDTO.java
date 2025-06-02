package com.example.redalert.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO para autenticação (login) de um usuário.")
public class UsuarioLoginDTO {

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Formato de email inválido.")
    @Schema(description = "DTO para autenticação (login) de um usuário.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Schema(description = "Senha de acesso do usuário.", example = "Senha@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String senha;
}
