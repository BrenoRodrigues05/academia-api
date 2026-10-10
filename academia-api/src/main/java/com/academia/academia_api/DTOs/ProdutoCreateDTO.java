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
@Schema(description = "Dados necessários para a criação de um novo Produto")
public class ProdutoCreateDTO {

    @Schema(
            description = "Nome do Produto",
            example = "Boné"
    )
    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 100, message = "Quantidade de caracteres precisa estar entre 3 e 100.")
    private String nome;

    @Schema(
            description = "Descrição do produto, tamanho, cor... etc.",
            example = "Boné vermelho tamanho P com logo da academia."

    )
    @Size(max = 500, message = "Quantidade de caracteres não pode ser acima de 500.")
    private String descricao;

    @Schema(
            description = "Preço do produto",
            example = "59.90"
    )
    @NotNull(message = "O preço é obrigatório.")
    @DecimalMin(
            value = "0.01",
            message = "O preço deve ser maior que zero."
    )
    @DecimalMax(
            value = "999.99",
            message = "O preço deve ser menor que R$ 999,99."
    )
    @Digits(integer = 8, fraction = 2,
            message = "O preço deve ter até 8 dígitos inteiros e 2 decimais.")
    private BigDecimal preco;

    @Schema(
            description = "Quantidade disponível em estoque",
            example = "50"
    )
    @NotNull(message = "O estoque é obrigatório.")
    @Min(value = 0, message = "O estoque não pode ser zero ou negativo.")
    private Integer estoque;

    @Schema(
            description = "URL da imagem do produto",
            example = "https://exemplo.com/bone.jpg"
    )
    @Size(max = 255,
            message = "A URL da imagem não pode ultrapassar 255 caracteres.")
    private String imagemUrl;
}
