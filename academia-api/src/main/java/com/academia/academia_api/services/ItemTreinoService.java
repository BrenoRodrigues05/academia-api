package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.ItemTreinoCreateDTO;
import com.academia.academia_api.DTOs.ItemTreinoResponseDTO;
import com.academia.academia_api.DTOs.ItemTreinoUpdateDTO;
import com.academia.academia_api.entity.Exercicio;
import com.academia.academia_api.entity.ItemTreino;
import com.academia.academia_api.entity.Treino;
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
            throw new RuntimeException("Id inválido.");
        }

        ItemTreino item = itemTreinoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item de treino não encontrado."));

        return itemTreinoMapper.toResponseDTO(item);
    }

    public List<ItemTreinoResponseDTO> findByTreino(Long treinoId) {

        if (treinoId == null || treinoId <= 0) {
            throw new RuntimeException("Treino inválido.");
        }

        return itemTreinoRepository.findByTreinoId(treinoId)
                .stream()
                .map(itemTreinoMapper::toResponseDTO)
                .toList();
    }

    public List<ItemTreinoResponseDTO> findByExercicio(Long exercicioId) {

        if (exercicioId == null || exercicioId <= 0) {
            throw new RuntimeException("Exercício inválido.");
        }

        return itemTreinoRepository.findByExercicioId(exercicioId)
                .stream()
                .map(itemTreinoMapper::toResponseDTO)
                .toList();
    }

    public ItemTreinoResponseDTO addItemTreino(ItemTreinoCreateDTO dto) {

        Treino treino = treinoRepository.findById(dto.getTreinoId())
                .orElseThrow(() -> new RuntimeException("Treino não encontrado."));

        Exercicio exercicio = exercicioRepository.findById(dto.getExercicioId())
                .orElseThrow(() -> new RuntimeException("Exercício não encontrado."));

        ItemTreino item = itemTreinoMapper.toEntity(dto);

        item.setTreino(treino);
        item.setExercicio(exercicio);

        ItemTreino salvo = itemTreinoRepository.save(item);

        return itemTreinoMapper.toResponseDTO(salvo);
    }

    public ItemTreinoResponseDTO updateItemTreino(Long id, ItemTreinoUpdateDTO dto) {

        if (id == null || id <= 0) {
            throw new RuntimeException("Id inválido.");
        }

        ItemTreino item = itemTreinoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item de treino não encontrado."));

        itemTreinoMapper.updateEntityFromDTO(dto, item);

        ItemTreino atualizado = itemTreinoRepository.save(item);

        return itemTreinoMapper.toResponseDTO(atualizado);
    }

    public ItemTreinoResponseDTO deleteItemTreino(Long id) {

        if (id == null || id <= 0) {
            throw new RuntimeException("Id inválido.");
        }

        ItemTreino item = itemTreinoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item de treino não encontrado."));

        itemTreinoRepository.delete(item);

        return itemTreinoMapper.toResponseDTO(item);
    }
}