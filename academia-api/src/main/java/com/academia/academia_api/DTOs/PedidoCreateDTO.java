package com.academia.academia_api.DTOs;
import com.academia.academia_api.entity.enums.MetodoPagamentoLojaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dados necessários para finalizar um pedido ou carrinho")
public class PedidoCreateDTO {

    @Schema(
            description = "Lista de itens do carrinho que serão incluídos no pedido.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty(message = "O pedido deve possuir pelo menos um item.")
    @Valid
    private List<ItemCarrinhoDTO> itens;

    @Schema(
            description = "Método de pagamento escolhido para finalizar o pedido.",
            example = "PIX",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "O método de pagamento é obrigatório.")
    private MetodoPagamentoLojaEnum metodoPagamento;
}