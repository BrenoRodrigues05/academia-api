package com.academia.academia_api.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExercicioResponseDTO {

    private Long id;

    private String nome;

    private String grupoMuscular;

    private String descricao;
}