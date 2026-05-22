package com.academia.academia_api.mappings;

import com.academia.academia_api.DTOs.MatriculaResponseDTO;
import com.academia.academia_api.DTOs.MatriculaUpdateDTO;
import com.academia.academia_api.DTOs.PlanoCreateDTO;
import com.academia.academia_api.entity.Matricula;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MatriculaMapper {

    MatriculaResponseDTO toResponseDTO(Matricula matricula);

    @Mapping(target = "matricula", ignore = true)
    Matricula toEntity(PlanoCreateDTO planoCreateDTO);

    @Mapping(target = "matricula", ignore = true)
    Matricula updateEntityFromDTO(MatriculaUpdateDTO updateDTO,  @MappingTarget Matricula entity);
}
