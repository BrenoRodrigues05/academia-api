package com.academia.academia_api.gateway;

import com.academia.academia_api.DTOs.PixCobrancaResponse;

import java.math.BigDecimal;

public interface PixGateway {
    PixCobrancaResponse gerarCobrancaPix(
            Long pagamentoId,
            BigDecimal valor,
            String descricao,
            String emailDevedor);
}
