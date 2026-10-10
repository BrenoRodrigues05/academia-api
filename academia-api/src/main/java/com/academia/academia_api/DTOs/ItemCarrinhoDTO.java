package com.academia.academia_api.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de um item enviado pelo carrinho do frontend")
public class ItemCarrinhoDTO {

    @Schema(
            description = "Identificador do produto que será adicionado ao carrinho.",
            example = "1"
    )
    @NotNull(message = "O ID do produto é obrigatório.")
    private Long produtoId;

    @Schema(
            description = "Quantidade de unidades do produto desejadas no carrinho.",
            example = "2",
            minimum = "1"
    )
    @NotNull(message = "A quantidade é obrigatória.")
    @Min(value = 1, message = "A quantidade deve ser pelo menos 1.")
    private Integer quantidade;
}