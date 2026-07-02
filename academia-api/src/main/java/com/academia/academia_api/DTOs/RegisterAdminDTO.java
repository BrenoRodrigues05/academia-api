package com.academia.academia_api.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados necessários para o cadastro de um novo administrador")
public record RegisterAdminDTO(

        @Schema(
                description = "Nome de usuário ou login que será utilizado para acesso ao sistema",
                example = "admin_central"
        )
        String login,

        @Schema(
                description = "Senha de acesso do administrador",
                example = "Senha@Forte123"
        )
        String senha
) {
}