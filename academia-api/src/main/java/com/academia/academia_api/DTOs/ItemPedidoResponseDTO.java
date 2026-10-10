package com.academia.academia_api.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de resposta dos itens que compõem um pedido")
public class ItemPedidoResponseDTO {

    @Schema(
            description = "Identificador do produto associado ao item do pedido.",
            example = "1"
    )
    private Long produtoId;

    @Schema(
            description = "Nome do produto associado ao item do pedido.",
            example = "Boné"
    )
    private String produtoNome;

    @Schema(
            description = "Quantidade de unidades do produto incluídas no pedido.",
            example = "2"
    )
    private Integer quantidade;

    @Schema(
            description = "Preço unitário do produto registrado no momento da compra.",
            example = "59.90"
    )
    private BigDecimal precoUnitario;
}