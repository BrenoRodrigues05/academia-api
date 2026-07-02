package com.academia.academia_api.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Estrutura padrão para respostas paginadas da API")
public record PageResponseDTO<T>(

        @Schema(
                description = "Lista contendo os elementos da página atual"
        )
        List<T> content,

        @Schema(
                description = "Número da página atual (baseada em zero)",
                example = "0"
        )
        int page,

        @Schema(
                description = "Quantidade de elementos máxima por página",
                example = "10"
        )
        int size,

        @Schema(
                description = "Quantidade total de elementos existentes no banco de dados",
                example = "45"
        )
        long totalElements,

        @Schema(
                description = "Quantidade total de páginas disponíveis para paginação",
                example = "5"
        )
        int totalPages
) {
}