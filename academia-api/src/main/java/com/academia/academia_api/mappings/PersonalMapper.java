package com.academia.academia_api.mappings;

import com.academia.academia_api.DTOs.PersonalCreateDTO;
import com.academia.academia_api.DTOs.PersonalResponseDTO;
import com.academia.academia_api.DTOs.PersonalUpdateDTO;
import com.academia.academia_api.entity.Personal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PersonalMapper {

    PersonalResponseDTO toResponseDTO(Personal personal);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    Personal toEntity(PersonalCreateDTO createDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "cref", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    void updateEntityFromDTO(PersonalUpdateDTO updateDTO, @MappingTarget Personal entity);
}
