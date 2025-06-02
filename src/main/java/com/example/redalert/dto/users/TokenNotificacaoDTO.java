package com.example.redalert.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO para enviar ou atualizar o token de notificação push do dispositivo do usuário.")
public class TokenNotificacaoDTO {

    @NotBlank(message = "O token de notificação é obrigatório.")
    @Schema(description = "Token do dispositivo fornecido pelo serviço de mensagens push (FCM/APNS).", 
            example = "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String notificationToken;
}
