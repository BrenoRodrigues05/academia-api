package com.academia.academia_api.mappings;

import com.academia.academia_api.DTOs.ProdutoCreateDTO;
import com.academia.academia_api.DTOs.ProdutoResponseDTO;
import com.academia.academia_api.DTOs.ProdutoUpdateDTO;
import com.academia.academia_api.entity.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProdutoMapper {

    ProdutoResponseDTO toResponseDTO(Produto produto);

    Produto toEntity(ProdutoCreateDTO createDTO);

    void updateEntityFromDTO(ProdutoUpdateDTO updateDTO, @MappingTarget Produto produto);
}