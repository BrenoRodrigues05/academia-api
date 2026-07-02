package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.enums.SexoEnum;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Dados necessários para a atualização de um aluno")
public class AlunoUpdateDTO {

    @Schema(
            description = "Nome atualizado do aluno",
            example = "João Silva Atualizado"
    )
    @NotBlank(message = "O nome não pode ser atualizado para um valor vazio.")
    @Size(max = 100)
    private String nome;

    @Schema(
            description = "Número de telefone atualizado",
            example = "81988887777"
    )
    @NotBlank(message = "O telefone não pode ser vazio.")
    @Size(max = 15)
    private String telefone;

    @Schema(
            description = "Gênero/Sexo do aluno",
            example = "MASCULINO"
    )
    @NotNull(message = "O sexo deve ser informado.")
    private SexoEnum sexo;
}
