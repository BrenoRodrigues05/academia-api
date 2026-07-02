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
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados necessários para a atualização de um exercício")
public class ExercicioUpdateDTO {

    @Schema(
            description = "Nome atualizado do exercício físico",
            example = "Supino Reto com Halteres"
    )
    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100)
    private String nome;

    @Schema(
            description = "Grupo muscular principal trabalhado",
            example = "Peitoral"
    )
    @NotBlank(message = "O grupo muscular é obrigatório.")
    @Size(max = 50)
    private String grupoMuscular;

    @Schema(
            description = "Descrição ou instruções de execução atualizadas",
            example = "Executado com halteres deitado no banco plano para melhor amplitude"
    )
    @Size(max = 500)
    private String descricao;
}