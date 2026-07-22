package com.academia.academia_api.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados necessários para a criação de uma nova matrícula")
public class MatriculaCreateDTO {

    @Schema(description = "ID do plano a ser vinculado", example = "1")
    @NotNull(message = "O plano é obrigatório.")
    private Long planoId;

    @Schema(description = "ID do aluno a ser vinculado", example = "3")
    @NotNull(message = "O aluno referente a matricula é obrigatório.")
    private Long alunoId;

    @Schema(
            description = "Define se a matrícula deve iniciar ativa ou inativa",
            example = "true"
    )
    private boolean ativa;
}
