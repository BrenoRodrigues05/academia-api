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
@AllArgsConstructor
@NoArgsConstructor
public class PersonalUpdateDTO {

    @NotBlank(message = "O nome não pode ser vazio.")
    @Size(max = 100)
    private String nome;

    @NotBlank(message = "O sobrenome não pode ser vazio.")
    @Size(max = 100)
    private String sobrenome;

    @NotBlank(message = "O telefone não pode ser vazio.")
    @Size(max = 15)
    private String telefone;

    @Size(max = 100)
    private String especialidade;

    @NotNull(message = "O sexo deve ser informado.")
    private SexoEnum sexo;
}
