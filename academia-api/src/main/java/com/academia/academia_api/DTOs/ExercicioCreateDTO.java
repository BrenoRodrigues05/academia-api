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
@Schema(description = "Dados necessários para a criação de um novo exercício")
public class ExercicioCreateDTO {

    @Schema(
            description = "Nome do exercício físico",
            example = "Supino Reto"
    )
    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String nome;

    @Schema(
            description = "Grupo muscular principal trabalhado no exercício",
            example = "Peitoral"
    )
    @NotBlank(message = "O grupo muscular é obrigatório.")
    @Size(max = 50, message = "O grupo muscular deve ter no máximo 50 caracteres.")
    private String grupoMuscular;

    @Schema(
            description = "Breve explicação ou instruções de execução do exercício",
            example = "Executado na barra ou com halteres deitado no banco plano"
    )
    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
    private String descricao;
}