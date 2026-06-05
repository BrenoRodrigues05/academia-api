package com.academia.academia_api.DTOs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TreinoResponseDTO {

    private Long id;

    private String nome;

    private String observacoes;

    private Boolean ativo = true;

    private Long personalId;
    private String nomePersonal;

    private Long alunoId;
    private String nomeAluno;
}
