package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.enums.SexoEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados necessários para o cadastro de um novo personal trainer")
public class PersonalCreateDTO {

    @Schema(
            description = "Primeiro nome do personal trainer",
            example = "Rodrigo"
    )
    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String nome;

    @Schema(
            description = "Sobrenome do personal trainer",
            example = "Oliveira"
    )
    @NotBlank(message = "O sobrenome é obrigatório.")
    @Size(max = 100, message = "O sobrenome deve ter no máximo 100 caracteres.")
    private String sobrenome;

    @Schema(
            description = "Endereço de e-mail do personal trainer",
            example = "rodrigo.personal@email.com"
    )
    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres.")
    private String email;

    @Schema(
            description = "Número de telefone ou celular para contato",
            example = "81988889999"
    )
    @NotBlank(message = "O telefone é obrigatório.")
    @Size(max = 15, message = "O telefone deve ter no máximo 15 caracteres.")
    private String telefone;

    @Schema(
            description = "Cédula de Identidade Profissional do CREF (Conselho Regional de Educação Física)",
            example = "123456-G/PE"
    )
    @NotBlank(message = "O CREF é obrigatório.")
    @Size(max = 20, message = "O CREF deve ter no máximo 20 caracteres.")
    private String cref;

    @Schema(
            description = "Principal foco de atuação profissional do personal",
            example = "Hipertrofia e Emagrecimento"
    )
    @Size(max = 100, message = "A especialidade deve ter no máximo 100 caracteres.")
    private String especialidade;

    @Schema(
            description = "Gênero/Sexo do personal trainer",
            example = "MASCULINO"
    )
    @NotNull(message = "O sexo é obrigatório.")
    private SexoEnum sexo;
}
