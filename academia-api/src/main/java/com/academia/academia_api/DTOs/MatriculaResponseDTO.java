package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.Plano;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de resposta detalhados de uma matrícula")
public class MatriculaResponseDTO {

    @Schema(
            description = "Identificador único ou código da matrícula",
            example = "10"
    )
    private Long matricula;

    @Schema(
            description = "Dados do plano vinculado à matrícula",
            example = "{\"id\": 1, \"nome\": \"Plano Anual\", \"preco\": 99.90}"
    )
    private PlanoResponseDTO plano;

    @Schema(
            description = "Dados do aluno vinculado à matrícula",
            example = "{\"id\": 3, \"nome\": \"João Silva\"}"
    )
    private AlunoResponseDTO aluno;

    @Schema(
            description = "Status de ativação da matrícula",
            example = "true"
    )
    private boolean ativa;
}
