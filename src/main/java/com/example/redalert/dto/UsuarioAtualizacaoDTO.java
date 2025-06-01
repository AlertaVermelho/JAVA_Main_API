package com.example.redalert.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioAtualizacaoDTO {

    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres.")
    private String nome;

    @Email(message = "Formato de email inválido.")
    @Size(max = 100, message = "O email não pode exceder 100 caracteres.")
    private String email;

    private String senhaAtual;

    @Size(min = 8, max = 100, message = "A nova senha deve ter entre 8 e 100 caracteres.")
    private String novaSenha;
}
