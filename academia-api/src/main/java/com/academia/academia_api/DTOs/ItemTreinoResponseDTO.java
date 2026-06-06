package com.academia.academia_api.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemTreinoResponseDTO {

    private Long id;

    private Integer series;

    private Integer repeticoes;

    private Integer descansoSegundos;

    private Long treinoId;

    private Long exercicioId;

    private String nomeExercicio;
}