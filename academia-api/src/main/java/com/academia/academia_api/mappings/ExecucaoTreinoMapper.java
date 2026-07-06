package com.academia.academia_api.mappings;

import com.academia.academia_api.DTOs.ExecucaoTreinoResponseDTO;
import com.academia.academia_api.entity.ExecucaoTreino;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExecucaoTreinoMapper {

    @Mapping(source = "treino.id", target = "treinoId")
    @Mapping(source = "treino.nome", target = "treino")
    ExecucaoTreinoResponseDTO toResponseDTO(ExecucaoTreino execucao);

}
