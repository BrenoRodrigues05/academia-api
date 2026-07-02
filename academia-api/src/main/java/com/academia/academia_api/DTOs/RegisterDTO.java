package com.academia.academia_api.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados genéricos necessários para o registro de credenciais de acesso")
public record RegisterDTO(

        @Schema(
                description = "Nome de usuário ou identificador de acesso",
                example = "usuario_academia"
        )
        @NotBlank
        String login,

        @Schema(
                description = "Senha de acesso para a conta",
                example = "senhaSegura123"
        )
        @NotBlank
        String senha
) {
}