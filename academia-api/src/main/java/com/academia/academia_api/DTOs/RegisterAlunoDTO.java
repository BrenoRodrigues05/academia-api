package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.enums.SexoEnum;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterAlunoDTO(@NotBlank
                                String login,

                               @NotBlank
                                @Size(min = 6)
                                String senha,

                               @NotBlank
                                String nome,

                               @Email
                                String email,

                               @Past
                               LocalDate dataNascimento,

                               @NotBlank
                                String telefone,

                               @NotNull
                               SexoEnum sexo) {
}
