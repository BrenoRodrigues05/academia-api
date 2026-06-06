package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.ExercicioCreateDTO;
import com.academia.academia_api.DTOs.ExercicioResponseDTO;
import com.academia.academia_api.DTOs.ExercicioUpdateDTO;
import com.academia.academia_api.entity.Exercicio;
import com.academia.academia_api.mappings.ExercicioMapper;
import com.academia.academia_api.repository.ExercicioRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExercicioService {

    private final ExercicioRepository exercicioRepository;
    private final ExercicioMapper exercicioMapper;

    public ExercicioService(
            ExercicioRepository exercicioRepository,
            ExercicioMapper exercicioMapper) {

        this.exercicioRepository = exercicioRepository;
        this.exercicioMapper = exercicioMapper;
    }

    public List<ExercicioResponseDTO> findAll() {

        return exercicioRepository.findAll()
                .stream()
                .map(exercicioMapper::toResponseDTO)
                .toList();
    }

    public ExercicioResponseDTO findById(Long id) {

        if (id == null || id <= 0) {
            throw new RuntimeException("Id inválido.");
        }

        Exercicio exercicio = exercicioRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Exercício não encontrado."));

        return exercicioMapper.toResponseDTO(exercicio);
    }

    public List<ExercicioResponseDTO> findByNome(String nome) {

        if (nome == null || nome.isBlank()) {
            throw new RuntimeException("Nome inválido.");
        }

        List<Exercicio> exercicios =
                exercicioRepository.findByNomeContainingIgnoreCase(nome);

        if (exercicios.isEmpty()) {
            throw new RuntimeException("Nenhum exercício encontrado.");
        }

        return exercicios.stream()
                .map(exercicioMapper::toResponseDTO)
                .toList();
    }

    public List<ExercicioResponseDTO> findByGrupoMuscular( String grupoMuscular)
    {
        if (grupoMuscular == null || grupoMuscular.isBlank()) {
            throw new RuntimeException("Grupo muscular inválido.");
        }

        List<Exercicio> exercicios =
                exercicioRepository.findByGrupoMuscularIgnoreCase(grupoMuscular);

        if (exercicios.isEmpty()) {
            throw new RuntimeException("Nenhum exercício encontrado.");
        }

        return exercicios.stream()
                .map(exercicioMapper::toResponseDTO)
                .toList();
    }

    public ExercicioResponseDTO addExercicio(@NonNull ExercicioCreateDTO dto)
    {
        if (exercicioRepository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new RuntimeException(
                    "Já existe um exercício com esse nome.");
        }

        Exercicio exercicio =
                exercicioMapper.toEntity(dto);

        Exercicio exercicioSalvo =
                exercicioRepository.save(exercicio);

        return exercicioMapper.toResponseDTO(exercicioSalvo);
    }

    public ExercicioResponseDTO updateExercicio(
            Long id,
            ExercicioUpdateDTO dto) {

        Exercicio exercicio = exercicioRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Exercício não encontrado."));

        exercicioMapper.updateEntityFromDTO(dto, exercicio);

        Exercicio atualizado =
                exercicioRepository.save(exercicio);

        return exercicioMapper.toResponseDTO(atualizado);
    }

    public ExercicioResponseDTO deleteExercicio(Long id) {

        Exercicio exercicio = exercicioRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Exercício não encontrado."));

        exercicioRepository.delete(exercicio);

        return exercicioMapper.toResponseDTO(exercicio);
    }
}