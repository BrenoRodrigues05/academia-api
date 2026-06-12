package com.academia.academia_api.infra.exceptions;
import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(
        LocalDateTime timestamp,
        Integer status,
        String error,
        Map<String, String> message,
        String path
) {
}