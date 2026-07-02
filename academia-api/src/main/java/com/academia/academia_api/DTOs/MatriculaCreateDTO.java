package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.Plano;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados necessários para a criação de uma nova matrícula")
public class MatriculaCreateDTO {

    @Schema(
            description = "Entidade do plano a ser vinculado à matrícula",
            example = "{\"id\": 1, \"nome\": \"Plano Anual\", \"preco\": 99.90}"
    )
    @NotNull(message = "O plano é obrigatório.")
    private Plano plano;

    @Schema(
            description = "Entidade do aluno que receberá a matrícula",
            example = "{\"id\": 3, \"nome\": \"João Silva\"}"
    )
    @NotNull(message = "O aluno referente a matricula é obrigatório.")
    private Aluno aluno;

    @Schema(
            description = "Define se a matrícula deve iniciar ativa ou inativa",
            example = "true"
    )
    private boolean ativa;
}
