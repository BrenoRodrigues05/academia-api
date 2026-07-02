package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.Plano;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados necessários para a atualização de uma matrícula")
public class MatriculaUpdateDTO {

    @Schema(
            description = "Dados do novo plano a ser associado à matrícula",
            example = "{\"id\": 2, \"nome\": \"Plano Mensal\", \"preco\": 129.90}"
    )
    @NotNull
    private Plano plano;

    @Schema(
            description = "Status de ativação atualizado da matrícula",
            example = "false"
    )
    private boolean ativa;
}
