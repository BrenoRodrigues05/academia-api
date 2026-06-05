package com.academia.academia_api.mappings;

import com.academia.academia_api.DTOs.ExercicioCreateDTO;
import com.academia.academia_api.DTOs.ExercicioResponseDTO;
import com.academia.academia_api.DTOs.ExercicioUpdateDTO;
import com.academia.academia_api.entity.Exercicio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ExercicioMapper {

    ExercicioResponseDTO toResponseDTO(Exercicio exercicio);

    @Mapping(target = "id", ignore = true)
    Exercicio toEntity(ExercicioCreateDTO createDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(ExercicioUpdateDTO updateDTO, @MappingTarget Exercicio entity);
}