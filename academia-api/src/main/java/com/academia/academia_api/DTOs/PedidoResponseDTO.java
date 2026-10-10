package com.academia.academia_api.DTOs;

import com.academia.academia_api.entity.enums.MetodoPagamentoLojaEnum;
import com.academia.academia_api.entity.enums.StatusPedidoEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados retornados pela API ao consultar um pedido")
public class PedidoResponseDTO {

    @Schema(
            description = "Identificador único do pedido.",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Identificador do aluno responsável pela realização do pedido.",
            example = "10"
    )
    private Long alunoId;

    @Schema(
            description = "Nome do aluno responsável pelo pedido.",
            example = "João Silva"
    )
    private String alunoNome;

    @Schema(
            description = "Lista de produtos, quantidades e preços unitários que compõem o pedido."
    )
    private List<ItemPedidoResponseDTO> itens;

    @Schema(
            description = "Valor total do pedido, calculado a partir dos itens.",
            example = "119.80"
    )
    private BigDecimal valorTotal;

    @Schema(
            description = "Situação atual do pedido, conforme os valores definidos no enum StatusPedidoEnum.",
            example = "PENDENTE"
    )
    private StatusPedidoEnum status;

    @Schema(
            description = "Método de pagamento utilizado ou selecionado para o pedido.",
            example = "PIX"
    )
    private MetodoPagamentoLojaEnum metodoPagamento;

    @Schema(
            description = "Data e hora em que o pedido foi criado.",
            example = "2026-10-10T14:30:00"
    )
    private LocalDateTime dataCriacao;

    @Schema(
            description = "Código PIX no formato copia e cola, quando disponível para o pedido.",
            example = "00020126580014BR.GOV.BCB.PIX...",
            nullable = true
    )
    private String pixCopiaECola;

    @Schema(
            description = "Conteúdo do QR Code PIX codificado em Base64, quando disponível.",
            example = "iVBORw0KGgoAAAANSUhEUg...",
            nullable = true
    )
    private String qrCodeBase64;
}