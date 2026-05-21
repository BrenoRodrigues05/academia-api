package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.Plano;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaCreateDTO {

    @NotNull(message = "O plano é obrigatório.")
    private Plano plano;
    @NotNull(message = "O aluno referente a matricula é obrigatório.")
    private Aluno aluno;
    private boolean ativa;
}
