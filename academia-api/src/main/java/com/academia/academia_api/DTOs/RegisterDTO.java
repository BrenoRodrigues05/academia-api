package com.academia.academia_api.DTOs;

import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(
        @NotBlank
        String login,

        @NotBlank
        String senha
) {
}
