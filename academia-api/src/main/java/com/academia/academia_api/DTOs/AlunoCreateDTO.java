package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.enums.SexoEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados necessários para a criação de um novo aluno")
public class AlunoCreateDTO {

    @Schema(
            description = "Nome completo do aluno",
            example = "João Silva"
    )
    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String nome;

    @Schema(
            description = "Endereço de e-mail do aluno",
            example = "joao.silva@email.com"
    )
    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres.")
    private String email;

    @Schema(
            description = "Data de nascimento do aluno",
            example = "2000-05-15"
    )
    @NotNull
    @Past(message = "A data de nascimento deve ser uma data no passado.")
    private LocalDate dataNascimento;

    @Schema(
            description = "Número de telefone ou celular para contato",
            example = "81999998888"
    )
    @NotBlank(message = "O telefone é obrigatório.")
    @Size(max = 15, message = "O telefone deve ter no máximo 15 caracteres.")
    private String telefone;

    @Schema(
            description = "Gênero/Sexo do aluno",
            example = "MASCULINO"
    )
    @NotNull
    private SexoEnum sexo;
}
