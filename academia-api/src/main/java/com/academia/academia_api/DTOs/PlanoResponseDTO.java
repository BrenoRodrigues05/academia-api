package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.enums.TipoPlano;
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
@Schema(description = "Dados de resposta detalhados de um plano")
public class PlanoResponseDTO {

    @Schema(
            description = "Identificador único do plano no sistema",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Nome comercial do plano",
            example = "Plano Gold Anual"
    )
    private String nome;

    @Schema(
            description = "Detalhamento dos benefícios e regras do plano",
            example = "Acesso livre a todas as unidades, musculação, área de cardio e todas as aulas coletivas."
    )
    private String descricao;

    @Schema(
            description = "Preço cobrado pelo plano",
            example = "89.90"
    )
    private BigDecimal valor;

    @Schema(
            description = "Periodicidade ou tipo de recorrência do plano",
            example = "ANUAL"
    )
    private TipoPlano tipo;

    @Schema(
            description = "Link ou URL da imagem associada ao plano",
            example = "https://cdn.academia.com/imagens/planos/gold.png"
    )
    private String imagemUrl;
}
