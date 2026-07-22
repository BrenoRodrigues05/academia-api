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
@Schema(description = "Dados necessários para associar um exercício a um treino com suas respectivas metas")
public class ItemTreinoCreateDTO {

    @Schema(
            description = "Quantidade de séries planejadas para o exercício",
            example = "4"
    )
    @NotNull(message = "A quantidade de séries é obrigatória.")
    @Min(value = 1, message = "A quantidade de séries deve ser maior que zero.")
    private Integer series;

    @Schema(
            description = "Número de repetições estipuladas por série",
            example = "12"
    )
    @NotNull(message = "A quantidade de repetições é obrigatória.")
    @Min(value = 1, message = "A quantidade de repetições deve ser maior que zero.")
    private Integer repeticoes;

    @Schema(
            description = "Tempo de intervalo entre as séries medido em segundos",
            example = "60"
    )
    @NotNull(message = "O descanso é obrigatório.")
    @Min(value = 0, message = "O descanso não pode ser negativo.")
    private Integer descansoSegundos;

    @Schema(
            description = "Identificador único do exercício vinculado",
            example = "5"
    )
    @NotNull(message = "O exercício é obrigatório.")
    private Long exercicioId;
}