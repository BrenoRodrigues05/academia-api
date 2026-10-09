package com.academia.academia_api.infra.payment.mercadoPago;
import com.academia.academia_api.DTOs.PixCobrancaResponse;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.gateway.PixGateway;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

@Component
public class MercadoPagoPixAdapter implements PixGateway {

    @Override
    public PixCobrancaResponse gerarCobrancaPix(Long pagamentoId, BigDecimal valor, String descricao, String emailDevedor) {
        try {

            String emailFormatado = (emailDevedor != null) ? emailDevedor.trim().toLowerCase() : "";

            if (emailFormatado.isBlank() || !emailFormatado.contains("@")) {
                throw new BadRequestException("O e-mail do pagador é inválido para gerar a cobrança Pix: " + emailDevedor);
            }

            PaymentClient client = new PaymentClient();

            PaymentPayerRequest payer = PaymentPayerRequest.builder()
                    .email(emailFormatado)
                    .build();

            OffsetDateTime dataExpiracao = OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(30);

            PaymentCreateRequest createRequest = PaymentCreateRequest.builder()
                    .transactionAmount(valor)
                    .description(descricao)
                    .paymentMethodId("pix")
                    .dateOfExpiration(dataExpiracao)
                    .payer(payer)
                    .build();

            MPRequestOptions requestOptions = MPRequestOptions.builder()
                    .customHeaders(Map.of("X-Idempotency-Key", UUID.randomUUID().toString()))
                    .build();

            Payment payment = client.create(createRequest, requestOptions);

            String pixCopiaECola = payment.getPointOfInteraction()
                    .getTransactionData()
                    .getQrCode();

            String qrCodeBase64 = payment.getPointOfInteraction()
                    .getTransactionData()
                    .getQrCodeBase64();

            LocalDateTime expiracaoLocal = payment.getDateOfExpiration()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            return new PixCobrancaResponse(
                    payment.getId().toString(),
                    pixCopiaECola,
                    qrCodeBase64,
                    payment.getTransactionAmount(),
                    expiracaoLocal
            );

        } catch (MPApiException e) {
            String detalheErro = e.getApiResponse() != null ? e.getApiResponse().getContent() : e.getMessage();
            System.err.println("===== ERRO MERCADO PAGO 400 =====");
            System.err.println(detalheErro);
            System.err.println("=================================");
            throw new BadRequestException("Erro na API do Mercado Pago.");
        } catch (Exception e) {
            throw new BadRequestException("Erro ao gerar a cobrança Pix no Mercado Pago.");
        }
    }
}