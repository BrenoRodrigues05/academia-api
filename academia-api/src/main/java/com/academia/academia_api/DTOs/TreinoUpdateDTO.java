package com.academia.academia_api.DTOs;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dados necessários para a atualização de uma ficha de treino")
public class TreinoUpdateDTO {

    @Schema(
            description = "Nome ou identificação atualizada da ficha de treino",
            example = "Treino A - Hipertrofia de Superiores (Foco em Ombros)"
    )
    @NotBlank
    @Size(max = 100)
    private String nome;

    @Schema(
            description = "Notas técnicas, recomendações ou observações atualizadas sobre o treino",
            example = "Aumentar a carga progressivamente nas duas últimas séries."
    )
    private String observacoes;

    @Schema(
            description = "Status de ativação modificado do treino no sistema",
            example = "true"
    )
    private Boolean ativo;
}
