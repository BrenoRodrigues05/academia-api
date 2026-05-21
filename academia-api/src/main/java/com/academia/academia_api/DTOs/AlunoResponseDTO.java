package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.enums.SexoEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlunoResponseDTO {
    private Long  id;
    private String nome;
    private String email;
    private LocalDate dataNascimento;
    private String telefone;
    private SexoEnum sexo;
}
