package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.enums.SexoEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de resposta detalhados de um aluno")
public class AlunoResponseDTO {

    @Schema(
            description = "Identificador único do aluno no sistema",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Nome completo do aluno",
            example = "João Silva"
    )
    private String nome;

    @Schema(
            description = "Endereço de e-mail do aluno",
            example = "joao.silva@email.com"
    )
    private String email;

    @Schema(
            description = "Data de nascimento do aluno",
            example = "2000-05-15"
    )
    private LocalDate dataNascimento;

    @Schema(
            description = "Número de telefone ou celular cadastrado",
            example = "81999998888"
    )
    private String telefone;

    @Schema(
            description = "Gênero/Sexo do aluno",
            example = "MASCULINO"
    )
    private SexoEnum sexo;
}
