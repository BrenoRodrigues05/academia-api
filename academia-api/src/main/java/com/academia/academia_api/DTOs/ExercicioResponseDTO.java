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
@Schema(description = "Dados de resposta detalhados de um exercício")
public class ExercicioResponseDTO {

    @Schema(
            description = "Identificador único do exercício no sistema",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Nome do exercício físico",
            example = "Supino Reto"
    )
    private String nome;

    @Schema(
            description = "Grupo muscular principal trabalhado",
            example = "Peitoral"
    )
    private String grupoMuscular;

    @Schema(
            description = "Instruções ou descrição detalhada do exercício",
            example = "Executado na barra ou com halteres deitado no banco plano"
    )
    private String descricao;
}