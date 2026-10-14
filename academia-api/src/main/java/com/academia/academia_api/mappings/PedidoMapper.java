package com.academia.academia_api.mappings;

import com.academia.academia_api.DTOs.PedidoResponseDTO;
import com.academia.academia_api.entity.Pedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ItemPedidoMapper.class})
public interface PedidoMapper {

    @Mapping(target = "alunoId", source = "aluno.id")
    @Mapping(target = "alunoNome", source = "aluno.nome")
    @Mapping(target = "pixCopiaECola", ignore = true)
    @Mapping(target = "qrCodeBase64", ignore = true)
    PedidoResponseDTO toResponseDTO(Pedido pedido);
}
