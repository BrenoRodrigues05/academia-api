package com.academia.academia_api.DTOs;

import jakarta.validation.constraints.NotNull;

public record ReatribuirPersonalDTO(
        @NotNull(message = "O ID do novo personal é obrigatório.")
        Long novoPersonalId
) {}
