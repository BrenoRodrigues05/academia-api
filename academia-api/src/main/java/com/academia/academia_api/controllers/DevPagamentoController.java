package com.academia.academia_api.controllers;

import com.academia.academia_api.services.PagamentoService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Profile("dev")
@RestController
@RequestMapping("/dev/pagamentos")
public class DevPagamentoController {

    private final PagamentoService pagamentoService;

    public DevPagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/{gatewayId}/aprovar")
    public ResponseEntity<String> aprovar(@PathVariable String gatewayId) {
        pagamentoService.confirmarPagamento(gatewayId);
        return ResponseEntity.ok("Pagamento " + gatewayId + " confirmado (simulado)");
    }
}