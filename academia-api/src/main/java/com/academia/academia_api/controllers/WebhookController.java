package com.academia.academia_api.controllers;

import com.academia.academia_api.services.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<Void> receber(
            @RequestBody Map<String, Object> body,
            @RequestParam(name = "data.id", required = false) String dataIdParam) {

        if (!"payment".equals(String.valueOf(body.get("type")))) {
            return ResponseEntity.ok().build();
        }

        String id = dataIdParam;
        if (id == null && body.get("data") instanceof Map<?, ?> data && data.get("id") != null) {
            id = data.get("id").toString();
        }

        if (id != null) {
            pagamentoService.processarNotificacaoMercadoPago(id);
        }
        return ResponseEntity.ok().build();
    }
}
