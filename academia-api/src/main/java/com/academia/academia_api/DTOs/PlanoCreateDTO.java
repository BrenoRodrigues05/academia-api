package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.enums.TipoPlano;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanoCreateDTO {
    @NotBlank(message = "O nome do plano é obrigatório.")
    private String nome;
    @NotBlank(message = "A descrição do plano é obrigatória.")
    private String descricao;
    @NotNull(message = "É necessário um valor para o plano.")
    private BigDecimal valor;
    @NotNull(message = "Selecione o tipo do plano.")
    private TipoPlano tipo;
    @NotBlank(message = "É necessária uma imagem para o plano.")
    private String imagemUrl;
}
