package com.academia.academia_api.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados necessários para a atualização de um plano")
public class PlanoUpdateDTO {

    @Schema(
            description = "Nome comercial atualizado do plano",
            example = "Plano Gold Anual V2"
    )
    @NotBlank(message = "É necessário um nome para o plano.")
    private String nome;

    @Schema(
            description = "Nova descrição detalhada dos benefícios do plano",
            example = "Acesso livre a todas as unidades, incluindo a nova área de Crossfit."
    )
    @NotBlank(message = "É necessária uma descrição para o plano.")
    private String descricao;

    @Schema(
            description = "Preço atualizado cobrado pelo plano",
            example = "99.90"
    )
    @NonNull
    @Positive(message = "O valor do plano precisa ser positivo.")
    private BigDecimal valor;

    @Schema(
            description = "Link ou URL atualizada da imagem do plano",
            example = "https://cdn.academia.com/imagens/planos/gold-v2.png"
    )
    @NotBlank(message = "É necessário uma imagem para o plano.")
    private String imagemUrl;
}
