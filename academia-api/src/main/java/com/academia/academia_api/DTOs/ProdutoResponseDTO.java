package com.academia.academia_api.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dados de resposta detalhados de um Produto.")
public class ProdutoResponseDTO {

    @Schema(
            description = "Identificador único do Produto.",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Nome do Produto.",
            example = "Boné"
    )
    private String nome;

    @Schema(
            description = "Descrição com detalhes do Produto.",
            example = "Boné vermelho com logo da academia"
    )
    private String descricao;

    @Schema(
            description = "Valor do Produto.",
            example = "59.90"
    )
    private BigDecimal preco;

    @Schema(
            description = "Quantidade atual do produto em estoque.",
            example = "50"
    )
    private Integer estoque;

    @Schema(
            description = "Imagem relacionada ao Produto.",
            example = "https://exemplo.com/bone.jpg"
    )
    private String imagemUrl;
}
