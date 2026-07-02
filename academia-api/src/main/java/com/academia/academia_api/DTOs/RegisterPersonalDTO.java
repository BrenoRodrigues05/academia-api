package com.academia.academia_api.DTOs;
import com.academia.academia_api.entity.enums.SexoEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados necessários para o registro/cadastro de um novo personal trainer com suas credenciais de acesso")
public record RegisterPersonalDTO(

        @Schema(
                description = "Nome de usuário exclusivo para o acesso do personal trainer",
                example = "rodrigo.personal"
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
                description = "Primeiro nome do personal trainer",
                example = "Rodrigo"
        )
        @NotBlank
        String nome,

        @Schema(
                description = "Sobrenome do personal trainer",
                example = "Oliveira"
        )
        @NotBlank
        String sobrenome,

        @Schema(
                description = "Endereço de e-mail para contato e notificações",
                example = "rodrigo.personal@email.com"
        )
        @Email
        String email,

        @Schema(
                description = "Número de telefone ou celular",
                example = "81988889999"
        )
        @NotBlank
        String telefone,

        @Schema(
                description = "Cédula de Identidade Profissional do CREF (Conselho Regional de Educação Física)",
                example = "123456-G/PE"
        )
        @NotBlank
        String cref,

        @Schema(
                description = "Principal foco ou especialidade de atuação profissional do personal",
                example = "Hipertrofia e Emagrecimento"
        )
        String Black,

        @Schema(
                description = "Gênero/Sexo do personal trainer",
                example = "MASCULINO"
        )
        @NotNull
        SexoEnum sexo
) {
}
