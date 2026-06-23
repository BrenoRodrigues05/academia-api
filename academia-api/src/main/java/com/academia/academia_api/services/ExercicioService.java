package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.ExercicioCreateDTO;
import com.academia.academia_api.DTOs.ExercicioResponseDTO;
import com.academia.academia_api.DTOs.ExercicioUpdateDTO;
import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.entity.Exercicio;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.mappings.ExercicioMapper;
import com.academia.academia_api.repository.ExercicioRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public PageResponseDTO<ExercicioResponseDTO> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Exercicio> exercicios = exercicioRepository.findAll(pageable);

        return new PageResponseDTO<>(
                exercicios.getContent().stream().map(exercicioMapper::toResponseDTO).toList(),
                exercicios.getNumber(),
                exercicios.getSize(),
                exercicios.getTotalElements(),
                exercicios.getTotalPages()
        );
    }

    public ExercicioResponseDTO findById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("O ID informado é inválido.");
        }

        Exercicio exercicio = exercicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado com o ID: " + id));

        return exercicioMapper.toResponseDTO(exercicio);
    }

    public List<ExercicioResponseDTO> findByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new BadRequestException("O nome informado para a busca é inválido.");
        }

        List<Exercicio> exercicios = exercicioRepository.findByNomeContainingIgnoreCase(nome);

        if (exercicios.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum exercício encontrado com o termo: " + nome);
        }

        return exercicios.stream()
                .map(exercicioMapper::toResponseDTO)
                .toList();
    }

    public List<ExercicioResponseDTO> findByGrupoMuscular(String grupoMuscular) {
        if (grupoMuscular == null || grupoMuscular.trim().isEmpty()) {
            throw new BadRequestException("O grupo muscular informado é inválido.");
        }

        List<Exercicio> exercicios = exercicioRepository.findByGrupoMuscularIgnoreCase(grupoMuscular);

        if (exercicios.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum exercício encontrado para o grupo muscular: " + grupoMuscular);
        }

        return exercicios.stream()
                .map(exercicioMapper::toResponseDTO)
                .toList();
    }

    public ExercicioResponseDTO addExercicio(@NonNull ExercicioCreateDTO dto) {
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            throw new BadRequestException("O nome do exercício não pode ser vazio.");
        }

        if (exercicioRepository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new BadRequestException("Já existe um exercício cadastrado com o nome: " + dto.getNome());
        }

        Exercicio exercicio = exercicioMapper.toEntity(dto);
        Exercicio exercicioSalvo = exercicioRepository.save(exercicio);

        return exercicioMapper.toResponseDTO(exercicioSalvo);
    }

    public ExercicioResponseDTO updateExercicio(Long id, ExercicioUpdateDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID inválido para atualização.");
        }

        Exercicio exercicio = exercicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado para atualização."));

        exercicioMapper.updateEntityFromDTO(dto, exercicio);
        Exercicio atualizado = exercicioRepository.save(exercicio);

        return exercicioMapper.toResponseDTO(atualizado);
    }

    public ExercicioResponseDTO deleteExercicio(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID inválido para exclusão.");
        }

        Exercicio exercicio = exercicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado para exclusão."));

        exercicioRepository.delete(exercicio);

        return exercicioMapper.toResponseDTO(exercicio);
    }
}