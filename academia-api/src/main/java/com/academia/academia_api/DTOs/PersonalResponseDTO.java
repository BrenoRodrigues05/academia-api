package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.enums.SexoEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados de resposta detalhados de um personal trainer")
public class PersonalResponseDTO {

    @Schema(
            description = "Identificador único do personal trainer no sistema",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Primeiro nome do personal trainer",
            example = "Rodrigo"
    )
    private String nome;

    @Schema(
            description = "Sobrenome do personal trainer",
            example = "Oliveira"
    )
    private String sobrenome;

    @Schema(
            description = "Endereço de e-mail registrado",
            example = "rodrigo.personal@email.com"
    )
    private String email;

    @Schema(
            description = "Número de telefone ou celular para contato",
            example = "81988889999"
    )
    private String telefone;

    @Schema(
            description = "Cédula de Identidade Profissional do CREF",
            example = "123456-G/PE"
    )
    private String cref;

    @Schema(
            description = "Principal especialidade ou foco de atuação do profissional",
            example = "Hipertrofia e Emagrecimento"
    )
    private String especialidade;

    @Schema(
            description = "Status que indica se o personal trainer está ativo no sistema",
            example = "true"
    )
    private Boolean ativo = true;

    @Schema(
            description = "Gênero/Sexo do personal trainer",
            example = "MASCULINO"
    )
    private SexoEnum sexo;
}
