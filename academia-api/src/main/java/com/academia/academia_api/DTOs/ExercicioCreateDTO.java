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
public class ExercicioCreateDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String nome;

    @NotBlank(message = "O grupo muscular é obrigatório.")
    @Size(max = 50, message = "O grupo muscular deve ter no máximo 50 caracteres.")
    private String grupoMuscular;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
    private String descricao;
}