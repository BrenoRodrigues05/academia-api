package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.enums.TipoPlano;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanoResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal valor;
    private TipoPlano tipo;
    private String imagemUrl;
}
