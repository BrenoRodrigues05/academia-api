package com.academia.academia_api.mappings;
import com.academia.academia_api.DTOs.TreinoCreateDTO;
import com.academia.academia_api.DTOs.TreinoResponseDTO;
import com.academia.academia_api.DTOs.TreinoUpdateDTO;
import com.academia.academia_api.entity.Treino;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TreinoMapper {

    @Mapping(source = "aluno.id", target = "alunoId")
    @Mapping(source = "aluno.nome", target = "nomeAluno")
    @Mapping(source = "personal.id", target = "personalId")
    @Mapping(source = "personal.nome", target = "nomePersonal")
    TreinoResponseDTO toResponseDTO(Treino treino);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "personal", ignore = true)
    @Mapping(target = "aluno", ignore = true)
    Treino toEntity(TreinoCreateDTO createDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personal", ignore = true)
    @Mapping(target = "aluno", ignore = true)
    void updateEntityFromDTO(TreinoUpdateDTO updateDTO, @MappingTarget Treino entity);

}
