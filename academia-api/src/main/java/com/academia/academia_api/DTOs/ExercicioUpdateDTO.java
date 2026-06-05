package com.academia.academia_api.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExercicioUpdateDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100)
    private String nome;

    @NotBlank(message = "O grupo muscular é obrigatório.")
    @Size(max = 50)
    private String grupoMuscular;

    @Size(max = 500)
    private String descricao;
}