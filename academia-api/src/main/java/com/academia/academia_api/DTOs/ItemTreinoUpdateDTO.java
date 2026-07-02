package com.academia.academia_api.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados necessários para a atualização de um item de treino")
public class ItemTreinoUpdateDTO {

    @Schema(
            description = "Quantidade atualizada de séries para o exercício",
            example = "4"
    )
    @NotNull
    @Min(1)
    private Integer series;

    @Schema(
            description = "Número atualizado de repetições por série",
            example = "10"
    )
    @NotNull
    @Min(1)
    private Integer repeticoes;

    @Schema(
            description = "Tempo atualizado de intervalo entre as séries em segundos",
            example = "45"
    )
    @NotNull
    @Min(0)
    private Integer descansoSegundos;
}