package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.enums.SexoEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalResponseDTO {

    private Long id;
    private String nome;
    private String sobrenome;
    private String email;
    private String telefone;
    private String cref;
    private String especialidade;
    private Boolean ativo = true;
    private SexoEnum sexo;
}
