package com.academia.academia_api.controllers;

import com.academia.academia_api.DTOs.MercadoPagoNotificationDTO;
import com.academia.academia_api.services.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks")
@Tag(name = "Webhooks", description = "Endpoints para recebimento de callbacks de gateways externas")
public class WebhookController {

    private final PagamentoService pagamentoService;

    public WebhookController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/mercadopago")
    @Operation(summary = "Recebe notificações de alteração de status de pagamento do Mercado Pago")
    public ResponseEntity<Void> receberNotificacaoMercadoPago(@RequestBody MercadoPagoNotificationDTO notificationDTO) {
        if(notificationDTO != null && "payment".equals(notificationDTO.type()) && notificationDTO.data() != null){
            pagamentoService.processarNotificacaoMercadoPago(notificationDTO.data().id());
        }
        return ResponseEntity.ok().build();
    }
}
