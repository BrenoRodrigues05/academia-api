package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.enums.SexoEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "Dados necessários para o registro/cadastro de um novo aluno")
public record RegisterAlunoDTO(

        @Schema(
                description = "Nome de usuário ou credencial exclusiva para o acesso do aluno",
                example = "joaosilva"
        )
        @NotBlank
        String login,

        @Schema(
                description = "Senha de acesso à conta, exigindo um nível mínimo de segurança",
                example = "mudar@123"
        )
        @NotBlank
        @Size(min = 6)
        String senha,

        @Schema(
                description = "Nome completo do aluno",
                example = "João Silva Sauro"
        )
        @NotBlank
        String nome,

        @Schema(
                description = "Endereço eletrônico para contato e notificações",
                example = "joao.silva@email.com"
        )
        @Email
        String email,

        @Schema(
                description = "Data de nascimento do aluno (deve ser uma data passada)",
                example = "1995-08-15"
        )
        @Past
        LocalDate dataNascimento,

        @Schema(
                description = "Número de telefone ou celular",
                example = "81999998888"
        )
        @NotBlank
        String telefone,

        @Schema(
                description = "Gênero/Sexo biológico do aluno",
                example = "MASCULINO"
        )
        @NotNull
        SexoEnum sexo
) {
}
