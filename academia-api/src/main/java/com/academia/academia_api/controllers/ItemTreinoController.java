package com.academia.academia_api.controllers;

import com.academia.academia_api.DTOs.ItemTreinoCreateDTO;
import com.academia.academia_api.DTOs.ItemTreinoResponseDTO;
import com.academia.academia_api.DTOs.ItemTreinoUpdateDTO;
import com.academia.academia_api.services.ItemTreinoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itens-treino")
public class ItemTreinoController {

    private final ItemTreinoService itemTreinoService;

    public ItemTreinoController(ItemTreinoService itemTreinoService) {
        this.itemTreinoService = itemTreinoService;
    }

    @GetMapping
    public ResponseEntity<List<ItemTreinoResponseDTO>> findAll() {
        return ResponseEntity.ok(itemTreinoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemTreinoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(itemTreinoService.findById(id));
    }

    @GetMapping("/treino/{treinoId}")
    public ResponseEntity<List<ItemTreinoResponseDTO>> findByTreino(@PathVariable Long treinoId) {
        return ResponseEntity.ok(itemTreinoService.findByTreino(treinoId));
    }

    @GetMapping("/exercicio/{exercicioId}")
    public ResponseEntity<List<ItemTreinoResponseDTO>> findByExercicio(@PathVariable Long exercicioId) {
        return ResponseEntity.ok(itemTreinoService.findByExercicio(exercicioId));
    }

    @PostMapping
    public ResponseEntity<ItemTreinoResponseDTO> addItemTreino(@Valid @RequestBody ItemTreinoCreateDTO dto) {
        ItemTreinoResponseDTO novoItem = itemTreinoService.addItemTreino(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(novoItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemTreinoResponseDTO> updateItemTreino(@PathVariable Long id, @Valid @RequestBody ItemTreinoUpdateDTO dto) {
        return ResponseEntity.ok(
                itemTreinoService.updateItemTreino(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ItemTreinoResponseDTO> deleteItemTreino(@PathVariable Long id) {
        return ResponseEntity.ok(
                itemTreinoService.deleteItemTreino(id)
        );
    }
}