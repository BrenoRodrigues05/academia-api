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
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dados necessários para a atualização cadastral de um personal trainer")
public class PersonalUpdateDTO {

    @Schema(
            description = "Nome atualizado do personal trainer",
            example = "Rodrigo"
    )
    @NotBlank(message = "O nome não pode ser vazio.")
    @Size(max = 100)
    private String nome;

    @Schema(
            description = "Sobrenome atualizado do personal trainer",
            example = "Oliveira Silva"
    )
    @NotBlank(message = "O sobrenome não pode ser vazio.")
    @Size(max = 100)
    private String sobrenome;

    @Schema(
            description = "Número de telefone ou celular atualizado",
            example = "81988889955"
    )
    @NotBlank(message = "O telefone não pode ser vazio.")
    @Size(max = 15)
    private String telefone;

    @Schema(
            description = "Especialidade ou foco de atuação atualizado do profissional",
            example = "Treinamento Funcional e Cardiorrespiratório"
    )
    @Size(max = 100)
    private String especialidade;

    @Schema(
            description = "Gênero/Sexo do personal trainer",
            example = "MASCULINO"
    )
    @NotNull(message = "O sexo deve ser informado.")
    private SexoEnum sexo;
}
