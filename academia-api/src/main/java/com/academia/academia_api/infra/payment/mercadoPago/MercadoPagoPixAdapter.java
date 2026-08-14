package com.academia.academia_api.infra.payment.mercadoPago;

import com.academia.academia_api.DTOs.PixCobrancaResponse;
import com.academia.academia_api.gateway.PixGateway;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.resources.payment.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Component
public class MercadoPagoPixAdapter  implements PixGateway {
    @Override
    public PixCobrancaResponse gerarCobrancaPix(Long pagamentoId, BigDecimal valor, String descricao, String emailDevedor) {

        try {
            PaymentClient client = new PaymentClient();

            PaymentPayerRequest payer = PaymentPayerRequest.builder()
                    .email(emailDevedor)
                    .build();

            OffsetDateTime dataExpiracao = OffsetDateTime.now().plusMinutes(30);

            PaymentCreateRequest createRequest = PaymentCreateRequest.builder()
                    .transactionAmount(valor)
                    .description(descricao)
                    .paymentMethodId("pix")
                    .dateOfExpiration(dataExpiracao)
                    .payer(payer)
                    .build();

            MPRequestOptions requestOptions = MPRequestOptions.builder()
                    .customHeaders(java.util.Map.of("X-Idempotency-Key", UUID.randomUUID().toString()))
                    .build();

            Payment payment = client.create(createRequest, requestOptions);

            String pixCopiaECola = payment.getPointOfInteraction()
                    .getTransactionData()
                    .getQrCode();

            String qrCodeBase64 = payment.getPointOfInteraction()
                    .getTransactionData()
                    .getQrCodeBase64();

            LocalDateTime expiracaoLocalDateTime = payment.getDateOfExpiration()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            return new PixCobrancaResponse(
                    payment.getId().toString(),
                    pixCopiaECola,
                    qrCodeBase64,
                    payment.getTransactionAmount(),
                    expiracaoLocalDateTime
            );
        }catch (Exception e) {
            throw new RuntimeException("Erro ao gerar cobrança Pix no Mercado Pago: " + e.getMessage(), e);
        }
    }
}
