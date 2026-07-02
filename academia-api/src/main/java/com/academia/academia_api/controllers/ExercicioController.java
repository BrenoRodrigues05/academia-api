package com.academia.academia_api.controllers;

import com.academia.academia_api.DTOs.ExercicioCreateDTO;
import com.academia.academia_api.DTOs.ExercicioResponseDTO;
import com.academia.academia_api.DTOs.ExercicioUpdateDTO;
import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.services.ExercicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Exercícios.",
        description = "Gerenciamento dos Exercícios da academia."
)
@RestController
@RequestMapping("/api/exercicios")
public class ExercicioController {

    private final ExercicioService exercicioService;

    public ExercicioController(ExercicioService exercicioService) {
        this.exercicioService = exercicioService;
    }

    @Operation(
            summary = "Listar Exercícios.",
            description = "Lista todos os exercícios com paginação, filtros e ordenação."
    )
    @GetMapping
    public ResponseEntity<PageResponseDTO<ExercicioResponseDTO>> findAll(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {
        return ResponseEntity.ok(exercicioService.findAll(page, size));
    }

    @Operation(
            summary = "Buscar Exercícios por Id."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ExercicioResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                exercicioService.findById(id));
    }

    @Operation(
            summary = "Buscar Exercícios por Nome."
    )
    @GetMapping("/busca-nome")
    public ResponseEntity<List<ExercicioResponseDTO>> findByNome(@RequestParam String nome) {
        return ResponseEntity.ok(
                exercicioService.findByNome(nome));
    }

    @Operation(
            summary = "Buscar Exercícios por Grupo Muscular."
    )
    @GetMapping("/grupo-muscular")
    public ResponseEntity<List<ExercicioResponseDTO>> findByGrupoMuscular(@RequestParam String grupoMuscular) {
        return ResponseEntity.ok(
                exercicioService.findByGrupoMuscular(grupoMuscular));
    }

    @Operation(
            summary = "Criar um novo Exercício."
    )
    @PostMapping
    public ResponseEntity<ExercicioResponseDTO> addExercicio(@Valid @RequestBody ExercicioCreateDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(exercicioService.addExercicio(dto));
    }

    @Operation(
            summary = "Atualizar um Exercício existente."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ExercicioResponseDTO> updateExercicio(@PathVariable Long id, @Valid @RequestBody ExercicioUpdateDTO dto)
    {
        return ResponseEntity.ok(
                exercicioService.updateExercicio(id, dto));
    }

    @Operation(
            summary = "Deletar um Exercício por Id."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ExercicioResponseDTO> deleteExercicio(@PathVariable Long id) {
        return ResponseEntity.ok(
                exercicioService.deleteExercicio(id));
    }
}