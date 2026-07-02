package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de resposta simplificados contendo informações cadastrais e de acesso de um usuário")
public record UsuarioResponseDTO(

        @Schema(
                description = "Identificador único do usuário no sistema",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Nome de usuário ou credencial utilizada para o login",
                example = "usuario_academia"
        )
        String login,

        @Schema(
                description = "Perfil ou nível de permissão de acesso atribuído ao usuário no sistema",
                example = "ALUNO"
        )
        UserRole role,

        @Schema(
                description = "Status que indica se a conta do usuário está ativa",
                example = "true"
        )
        Boolean ativo
) {
}