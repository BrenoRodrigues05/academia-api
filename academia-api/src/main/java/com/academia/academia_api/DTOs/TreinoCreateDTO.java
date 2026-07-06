package com.academia.academia_api.DTOs;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados necessários para a criação de um novo treino")
public class TreinoCreateDTO {

    @Schema(
            description = "Nome ou identificação da ficha de treino",
            example = "Treino A - Hipertrofia de Superiores"
    )
    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String nome;

    @Schema(
            description = "Notas técnicas, recomendações ou observações específicas sobre o treino",
            example = "Focar na cadência do movimento e realizar aquecimento no manguito antes de iniciar."
    )
    private String observacoes;

    @Schema(
            description = "Identificador único do aluno que receberá a ficha de treino",
            example = "12"
    )
    @NotNull(message = "É necessário selecionar um aluno.")
    private Long alunoId;

    @Schema(
            description = "Status que indica data de incio do treino",
            example = "06/07/2026"
    )
    @NotNull(message = "A data de inicio é obrigatória.")
    private LocalDate dataInicio;

    @Schema(
            description = "Status que indica data de fim do treino",
            example = "06/08/2026"
    )
    @NotNull(message = "A data final é obrigatória.")
    private LocalDate dataFim;
}
