package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.ItemTreinoResponseDTO;
import com.academia.academia_api.DTOs.ItemTreinoUpdateDTO;
import com.academia.academia_api.entity.ItemTreino;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.mappings.ItemTreinoMapper;
import com.academia.academia_api.repository.ExercicioRepository;
import com.academia.academia_api.repository.ItemTreinoRepository;
import com.academia.academia_api.repository.TreinoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemTreinoService {

    private final ItemTreinoRepository itemTreinoRepository;
    private final TreinoRepository treinoRepository;
    private final ExercicioRepository exercicioRepository;
    private final ItemTreinoMapper itemTreinoMapper;

    public ItemTreinoService(
            ItemTreinoRepository itemTreinoRepository,
            TreinoRepository treinoRepository,
            ExercicioRepository exercicioRepository,
            ItemTreinoMapper itemTreinoMapper) {

        this.itemTreinoRepository = itemTreinoRepository;
        this.treinoRepository = treinoRepository;
        this.exercicioRepository = exercicioRepository;
        this.itemTreinoMapper = itemTreinoMapper;
    }

    public List<ItemTreinoResponseDTO> findAll() {
        return itemTreinoRepository.findAll()
                .stream()
                .map(itemTreinoMapper::toResponseDTO)
                .toList();
    }

    public ItemTreinoResponseDTO findById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("O ID informado é inválido.");
        }

        ItemTreino item = itemTreinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item de treino não encontrado com o ID: " + id));

        return itemTreinoMapper.toResponseDTO(item);
    }

    public List<ItemTreinoResponseDTO> findByTreino(Long treinoId) {
        if (treinoId == null || treinoId <= 0) {
            throw new BadRequestException("O ID do treino informado é inválido.");
        }

        // Validação preventiva: Garante que o treino realmente existe antes de buscar os itens
        if (!treinoRepository.existsById(treinoId)) {
            throw new ResourceNotFoundException("Treino não encontrado com o ID: " + treinoId);
        }

        return itemTreinoRepository.findByTreinoId(treinoId)
                .stream()
                .map(itemTreinoMapper::toResponseDTO)
                .toList();
    }

    public List<ItemTreinoResponseDTO> findByExercicio(Long exercicioId) {
        if (exercicioId == null || exercicioId <= 0) {
            throw new BadRequestException("O ID do exercício informado é inválido.");
        }

        if (!exercicioRepository.existsById(exercicioId)) {
            throw new ResourceNotFoundException("Exercício não encontrado com o ID: " + exercicioId);
        }

        return itemTreinoRepository.findByExercicioId(exercicioId)
                .stream()
                .map(itemTreinoMapper::toResponseDTO)
                .toList();
    }

    public ItemTreinoResponseDTO updateItemTreino(Long id, ItemTreinoUpdateDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("O ID informado é inválido para atualização.");
        }

        ItemTreino item = itemTreinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item de treino não encontrado para atualização."));

        itemTreinoMapper.updateEntityFromDTO(dto, item);
        ItemTreino atualizado = itemTreinoRepository.save(item);

        return itemTreinoMapper.toResponseDTO(atualizado);
    }

    public ItemTreinoResponseDTO deleteItemTreino(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("O ID informado é inválido para exclusão.");
        }

        ItemTreino item = itemTreinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item de treino não encontrado para exclusão."));

        itemTreinoRepository.delete(item);
        return itemTreinoMapper.toResponseDTO(item);
    }
}