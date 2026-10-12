package com.academia.academia_api.mappings;

import com.academia.academia_api.DTOs.ItemPedidoResponseDTO;
import com.academia.academia_api.entity.ItemPedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemPedidoMapper {

    @Mapping(target = "produtoId", source = "produto.id")
    @Mapping(target = "produtoNome", source = "produto.nome")
    ItemPedidoResponseDTO toResponseDTO(ItemPedido itemPedido);
}
