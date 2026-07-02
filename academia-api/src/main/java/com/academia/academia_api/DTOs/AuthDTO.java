package com.academia.academia_api.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados necessários para realizar a autenticação no sistema")
public record AuthDTO(

        @Schema(
                description = "Login do usuário",
                example = "superadmin"
        )
        String login,

        @Schema(
                description = "Senha",
                example = "Academia@2026!"
        )
        String senha
) {
}