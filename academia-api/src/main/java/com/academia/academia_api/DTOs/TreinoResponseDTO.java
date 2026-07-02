package com.academia.academia_api.DTOs;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dados de resposta detalhados de um treino")
public class TreinoResponseDTO {

    @Schema(
            description = "Identificador único do treino no sistema",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Nome ou identificação da ficha de treino",
            example = "Treino A - Hipertrofia de Superiores"
    )
    private String nome;

    @Schema(
            description = "Notas técnicas ou observações gerais sobre a rotina de exercícios",
            example = "Focar na cadência do movimento e realizar aquecimento prévio."
    )
    private String observacoes;

    @Schema(
            description = "Status que indica se o treino está ativo e disponível para o aluno",
            example = "true"
    )
    private Boolean ativo = true;

    @Schema(
            description = "Identificador único do personal trainer que prescreveu o treino",
            example = "3"
    )
    private Long personalId;

    @Schema(
            description = "Nome completo do personal trainer responsável",
            example = "Rodrigo Oliveira"
    )
    private String nomePersonal;

    @Schema(
            description = "Identificador único do aluno vinculado ao treino",
            example = "12"
    )
    private Long alunoId;

    @Schema(
            description = "Nome completo do aluno proprietário do treino",
            example = "João Silva Sauro"
    )
    private String nomeAluno;
}
