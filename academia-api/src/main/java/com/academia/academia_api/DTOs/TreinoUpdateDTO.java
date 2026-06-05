package com.academia.academia_api.DTOs;
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
public class TreinoUpdateDTO {

    @NotBlank
    @Size(max = 100)
    private String nome;
    private String observacoes;
    private Boolean ativo;
}
