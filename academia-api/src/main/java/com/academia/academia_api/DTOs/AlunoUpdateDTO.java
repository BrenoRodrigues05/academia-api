package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.enums.SexoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlunoUpdateDTO {
    @NotBlank(message = "O nome não pode ser atualizado para um valor vazio.")
    @Size(max = 100)
    private String nome;

    @NotBlank(message = "O telefone não pode ser vazio.")
    @Size(max = 15)
    private String telefone;

    @NotNull(message = "O sexo deve ser informado.")
    private SexoEnum sexo;
}
