package com.academia.academia_api.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanoUpdateDTO {
    @NotBlank(message = "É necessário um nome para o plano.")
    private String nome;
    @NotBlank(message = "É necessária uma descrição para o plano.")
    private String descricao;
    @NonNull
    private BigDecimal valor;
    @NotBlank(message = "É necessário uma imagem para o plano.")
    private String imagemUrl;
}
