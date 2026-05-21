package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.Plano;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaUpdateDTO {
    @NotNull
    private Plano plano;
    private boolean ativa;
}
