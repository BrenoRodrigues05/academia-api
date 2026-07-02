package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.enums.TipoPlano;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dados necessários para a criação de um novo plano da academia")
public class PlanoCreateDTO {

    @Schema(
            description = "Nome comercial do plano",
            example = "Plano Gold Anual"
    )
    @NotBlank(message = "O nome do plano é obrigatório.")
    private String nome;

    @Schema(
            description = "Detalhamento dos benefícios e regras do plano",
            example = "Acesso livre a todas as unidades, musculação, área de cardio e todas as aulas coletivas."
    )
    @NotBlank(message = "A descrição do plano é obrigatória.")
    private String descricao;

    @Schema(
            description = "Preço mensal ou integral cobrado pelo plano",
            example = "89.90"
    )
    @NotNull(message = "É necessário um valor para o plano.")
    @Positive(message = "O valor do plano precisa ser positivo.")
    private BigDecimal valor;

    @Schema(
            description = "Periodicidade ou tipo de recorrência do plano",
            example = "ANUAL"
    )
    @NotNull(message = "Selecione o tipo do plano.")
    private TipoPlano tipo;

    @Schema(
            description = "Link ou caminho da imagem promocional ou representativa do plano",
            example = "https://cdn.academia.com/imagens/planos/gold.png"
    )
    @NotBlank(message = "É necessária uma imagem para o plano.")
    private String imagemUrl;
}
