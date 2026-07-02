package com.academia.academia_api.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de resposta detalhados de um item de treino")
public class ItemTreinoResponseDTO {

    @Schema(
            description = "Identificador único do item de treino",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Quantidade de séries planejadas",
            example = "4"
    )
    private Integer series;

    @Schema(
            description = "Número de repetições por série",
            example = "12"
    )
    private Integer repeticoes;

    @Schema(
            description = "Tempo de intervalo entre as séries em segundos",
            example = "60"
    )
    private Integer descansoSegundos;

    @Schema(
            description = "Identificador único do treino vinculado",
            example = "2"
    )
    private Long treinoId;

    @Schema(
            description = "Identificador único do exercício vinculado",
            example = "5"
    )
    private Long exercicioId;

    @Schema(
            description = "Nome do exercício vinculado",
            example = "Supino Reto"
    )
    private String nomeExercicio;
}