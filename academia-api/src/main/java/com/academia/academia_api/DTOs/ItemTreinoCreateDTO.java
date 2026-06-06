package com.academia.academia_api.DTOs;

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
public class ItemTreinoCreateDTO {

    @NotNull(message = "A quantidade de séries é obrigatória.")
    @Min(value = 1, message = "A quantidade de séries deve ser maior que zero.")
    private Integer series;

    @NotNull(message = "A quantidade de repetições é obrigatória.")
    @Min(value = 1, message = "A quantidade de repetições deve ser maior que zero.")
    private Integer repeticoes;

    @NotNull(message = "O descanso é obrigatório.")
    @Min(value = 0, message = "O descanso não pode ser negativo.")
    private Integer descansoSegundos;

    @NotNull(message = "O treino é obrigatório.")
    private Long treinoId;

    @NotNull(message = "O exercício é obrigatório.")
    private Long exercicioId;
}