package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.enums.UserRole;

public record UsuarioResponseDTO(
        Long id,
        String login,
        UserRole role,
        Boolean ativo
) {
}