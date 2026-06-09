package com.academia.academia_api.DTOs;
import com.academia.academia_api.entity.enums.SexoEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterPersonalDTO(@NotBlank
                                  String login,
                                  @NotBlank
                                  @Size(min = 6)
                                  String senha,
                                  @NotBlank
                                  String nome,
                                  @NotBlank
                                  String sobrenome,
                                  @Email
                                  String email,
                                  @NotBlank
                                  String telefone,
                                  @NotBlank
                                  String cref,
                                  String especialidade,
                                  @NotNull
                                  SexoEnum sexo) {
}
