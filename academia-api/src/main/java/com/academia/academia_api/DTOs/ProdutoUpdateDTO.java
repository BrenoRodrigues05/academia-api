package com.academia.academia_api.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados necessários para a atualização de um Produto")
public class ProdutoUpdateDTO {

    @Schema(
            description = "Novo nome do produto.",
            example = "Camiseta da Academia"
    )
    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 100,
            message = "O nome deve ter entre 3 e 100 caracteres.")
    private String nome;

    @Schema(
            description = "Nova descrição do produto, com informações sobre suas características.",
            example = "Camiseta preta tamanho M com logo da academia."
    )
    @Size(max = 500,
            message = "A descrição não pode ultrapassar 500 caracteres.")
    private String descricao;

    @Schema(
            description = "Novo preço unitário do produto, maior que zero.",
            example = "79.90"
    )
    @NotNull(message = "O preço é obrigatório.")
    @DecimalMin(value = "0.01",
            message = "O preço deve ser maior que zero.")
    @Digits(integer = 8, fraction = 2,
            message = "O preço deve ter até 8 dígitos inteiros e 2 decimais.")
    private BigDecimal preco;

    @Schema(
            description = "Nova quantidade disponível do produto em estoque.",
            example = "25",
            minimum = "0"
    )
    @NotNull(message = "O estoque é obrigatório.")
    @Min(value = 0, message = "O estoque não pode ser negativo.")
    private Integer estoque;

    @Schema(
            description = "Nova URL da imagem do produto.",
            example = "https://exemplo.com/imagens/camiseta.jpg",
            maxLength = 255
    )
    @Size(max = 255,
            message = "A URL da imagem não pode ultrapassar 255 caracteres.")
    private String imagemUrl;
}
