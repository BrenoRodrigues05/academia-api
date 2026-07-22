package com.academia.academia_api.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados necessários para a atualização de uma matrícula")
public class MatriculaUpdateDTO {

    @Schema(description = "ID do novo plano (se for alterar)", example = "2")
    @NotNull(message = "O plano é obrigatório.")
    private Long planoId;

    @Schema(
            description = "Status de ativação atualizado da matrícula",
            example = "false"
    )
    private boolean ativa;
}
