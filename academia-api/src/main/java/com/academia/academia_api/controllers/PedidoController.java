package com.academia.academia_api.controllers;

import com.academia.academia_api.DTOs.PedidoCreateDTO;
import com.academia.academia_api.DTOs.PedidoResponseDTO;
import com.academia.academia_api.entity.enums.StatusPagamentoEnum;
import com.academia.academia_api.services.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Operation(
            summary = "Buscar Pedidos do aluno logado."
    )
    @GetMapping("/meus-pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> listarMeusPedidos() {
        List<PedidoResponseDTO> pedidos = pedidoService.listarPedidosDoAlunoLogado();
        return ResponseEntity.ok(pedidos);
    }

    @Operation(
            summary = "Buscar pedido de acordo com o status de Pagamento."
    )
    @GetMapping("/status-pagamento")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorStatusDePagamento(@RequestParam StatusPagamentoEnum status) {
        List<PedidoResponseDTO> pedidos = pedidoService.getByStatus(status);
        return ResponseEntity.ok(pedidos);
    }

    @Operation(
            summary = "Criar um novo Pedido."
    )
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(@RequestBody @Valid PedidoCreateDTO dto) {
        PedidoResponseDTO pedidoCriado = pedidoService.create(dto);
         return  ResponseEntity.ok(pedidoCriado);
    }

}