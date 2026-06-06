package com.academia.academia_api.mappings;

import com.academia.academia_api.DTOs.ItemTreinoCreateDTO;
import com.academia.academia_api.DTOs.ItemTreinoResponseDTO;
import com.academia.academia_api.DTOs.ItemTreinoUpdateDTO;
import com.academia.academia_api.entity.ItemTreino;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ItemTreinoMapper {

    @Mapping(target = "treinoId", source = "treino.id")
    @Mapping(target = "exercicioId", source = "exercicio.id")
    @Mapping(target = "nomeExercicio", source = "exercicio.nome")
    ItemTreinoResponseDTO toResponseDTO(ItemTreino itemTreino);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "treino", ignore = true)
    @Mapping(target = "exercicio", ignore = true)
    ItemTreino toEntity(ItemTreinoCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "treino", ignore = true)
    @Mapping(target = "exercicio", ignore = true)
    void updateEntityFromDTO(ItemTreinoUpdateDTO dto, @MappingTarget ItemTreino entity);
}