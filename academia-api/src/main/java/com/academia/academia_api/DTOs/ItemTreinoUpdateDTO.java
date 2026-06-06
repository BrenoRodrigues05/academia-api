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
public class ItemTreinoUpdateDTO {

    @NotNull
    @Min(1)
    private Integer series;

    @NotNull
    @Min(1)
    private Integer repeticoes;

    @NotNull
    @Min(0)
    private Integer descansoSegundos;
}