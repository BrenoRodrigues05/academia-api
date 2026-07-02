package com.academia.academia_api.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de resposta retornados após uma autenticação bem-sucedida")
public record LoginResponseDTO(

        @Schema(
                description = "Token de acesso JWT gerado para autenticar as requisições subsequentes",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzdXBlcmFkbWluIiwiZXhwIjoxNzE5OTYwMDAwfQ.signature"
        )
        String token
) {
}
