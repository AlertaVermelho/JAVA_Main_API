package com.example.redalert.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenNotificacaoDTO {

    @NotBlank(message = "O token de notificação é obrigatório.")
    private String notificationToken;
}
