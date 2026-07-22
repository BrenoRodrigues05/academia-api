package com.academia.academia_api.controllers;

import com.academia.academia_api.DTOs.ItemTreinoResponseDTO;
import com.academia.academia_api.DTOs.ItemTreinoUpdateDTO;
import com.academia.academia_api.services.ItemTreinoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "ItensTreino.",
        description = "Gerenciamento dos itens de cada Treino."
)
@RestController
@RequestMapping("/api/itens-treino")
public class ItemTreinoController {

    private final ItemTreinoService itemTreinoService;

    public ItemTreinoController(ItemTreinoService itemTreinoService) {
        this.itemTreinoService = itemTreinoService;
    }

    @Operation(
            summary = "Listar Itens de treino.",
            description = "Lista todos os itens com paginação, filtros e ordenação."
    )
    @GetMapping
    public ResponseEntity<List<ItemTreinoResponseDTO>> findAll() {
        return ResponseEntity.ok(itemTreinoService.findAll());
    }

    @Operation(
            summary = "Buscar itens por Id."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ItemTreinoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(itemTreinoService.findById(id));
    }

    @Operation(
            summary = "Buscar itens por Treino.",
            description = "Lista os itens de cada treino específico."
    )
    @GetMapping("/treino/{treinoId}")
    public ResponseEntity<List<ItemTreinoResponseDTO>> findByTreino(@PathVariable Long treinoId) {
        return ResponseEntity.ok(itemTreinoService.findByTreino(treinoId));
    }

    @Operation(
            summary = "Buscar itens por Exercicio.",
            description = "Lista os itens de cada Exercício específico."
    )
    @GetMapping("/exercicio/{exercicioId}")
    public ResponseEntity<List<ItemTreinoResponseDTO>> findByExercicio(@PathVariable Long exercicioId) {
        return ResponseEntity.ok(itemTreinoService.findByExercicio(exercicioId));
    }

    @Operation(
            summary = "Edita um Item existente."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ItemTreinoResponseDTO> updateItemTreino(@PathVariable Long id, @Valid @RequestBody ItemTreinoUpdateDTO dto) {
        return ResponseEntity.ok(
                itemTreinoService.updateItemTreino(id, dto)
        );
    }

    @Operation(
            summary = "Deleta um Item existente."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ItemTreinoResponseDTO> deleteItemTreino(@PathVariable Long id) {
        return ResponseEntity.ok(
                itemTreinoService.deleteItemTreino(id)
        );
    }
}