package com.academia.academia_api.mappings;

import com.academia.academia_api.DTOs.PlanoCreateDTO;
import com.academia.academia_api.DTOs.PlanoResponseDTO;
import com.academia.academia_api.DTOs.PlanoUpdateDTO;
import com.academia.academia_api.entity.Plano;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PlanoMapper {

    PlanoResponseDTO toResponseDTO(Plano plano);

    @Mapping(target = "id", ignore = true)
    Plano toEntity(PlanoCreateDTO createDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tipo", ignore = true)
    void  updateEntityFromDTO(PlanoUpdateDTO updateDTO, @MappingTarget Plano entity);
}
