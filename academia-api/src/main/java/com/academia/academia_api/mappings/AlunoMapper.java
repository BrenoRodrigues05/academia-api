package com.academia.academia_api.mappings;

import com.academia.academia_api.DTOs.AlunoCreateDTO;
import com.academia.academia_api.DTOs.AlunoResponseDTO;
import com.academia.academia_api.DTOs.AlunoUpdateDTO;
import com.academia.academia_api.entity.Aluno;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AlunoMapper {

    AlunoResponseDTO toResponseDTO(Aluno aluno);

    @Mapping(target = "id", ignore = true)
    Aluno toEntity(AlunoCreateDTO createDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "dataNascimento", ignore = true)
    void updateEntityFromDTO(AlunoUpdateDTO updateDTO, @MappingTarget Aluno entity);
}
