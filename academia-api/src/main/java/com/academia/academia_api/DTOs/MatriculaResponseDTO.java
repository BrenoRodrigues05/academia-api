package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.Plano;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaResponseDTO {

    private Long matricula;
    private Plano plano;
    private Aluno aluno;
    private boolean ativa;
}
