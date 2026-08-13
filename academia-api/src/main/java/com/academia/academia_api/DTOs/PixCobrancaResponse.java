package com.academia.academia_api.DTOs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PixCobrancaResponse(
        String transacaoId,
        String codigoPix,
        String qrCodeBase64,
        BigDecimal valor,
        LocalDateTime dataexpiracao
) { }
