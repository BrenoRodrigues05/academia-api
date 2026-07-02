package com.academia.academia_api.controllers;

import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.DTOs.TreinoCreateDTO;
import com.academia.academia_api.DTOs.TreinoResponseDTO;
import com.academia.academia_api.DTOs.TreinoUpdateDTO;
import com.academia.academia_api.services.TreinoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Treinos.",
        description = "Gerenciamento dos treinos da academia."
)
@RestController
@RequestMapping("/api/treinos")
public class TreinoController {

    private final TreinoService treinoService;

    public TreinoController(TreinoService treinoService) {
        this.treinoService = treinoService;
    }

    @Operation(
            summary = "Listar Treinos.",
            description = "Lista todos os treinos com paginação, filtros e ordenação."
    )
    @GetMapping
    public ResponseEntity<PageResponseDTO<TreinoResponseDTO>> findAll(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {
        return ResponseEntity.ok(treinoService.findAll(page, size));
    }

    @Operation(
            summary = "Buscar treino por ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<TreinoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(treinoService.findById(id));
    }

    @Operation(
            summary = "Buscar treinos ativos."
    )
    @GetMapping("/ativos")
    public ResponseEntity<List<TreinoResponseDTO>> findByAtivoTrue() {
        return ResponseEntity.ok(treinoService.findByAtivoTrue());
    }

    @Operation(
            summary = "Buscar treinos inativos."
    )
    @GetMapping("/inativos")
    public ResponseEntity<List<TreinoResponseDTO>> findByAtivoFalse() {
        return ResponseEntity.ok(treinoService.findByAtivoFalse());
    }

    @Operation(
            summary = "Buscar treino por Nome."
    )
    @GetMapping("/busca-nome")
    public ResponseEntity<List<TreinoResponseDTO>> findByNome(
            @RequestParam String nome) {

        return ResponseEntity.ok(treinoService.findByNome(nome));
    }

    @Operation(
            summary = "Buscar treino por Personal.",
            description = "Busca e lista todos os treinos criados por um personal específico."
    )
    @GetMapping("/personal/{personalId}")
    public ResponseEntity<List<TreinoResponseDTO>> findByPersonal(
            @PathVariable Long personalId) {

        return ResponseEntity.ok(treinoService.findByPersonal(personalId));
    }

    @Operation(
            summary = "Buscar treino por Alunos.",
            description = "Busca e lista todos os treinos de um aluno específico."
    )
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<TreinoResponseDTO> findTreinoAtivoAluno(
            @PathVariable Long alunoId) {

        return ResponseEntity.ok(
                treinoService.findTreinoAtivoAluno(alunoId)
        );
    }

    @Operation(
            summary = "Cria um novo treino."
    )
    @PostMapping
    public ResponseEntity<TreinoResponseDTO> addTreino(
            @Valid @RequestBody TreinoCreateDTO dto) {

        TreinoResponseDTO treinoCriado =
                treinoService.addTreino(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(treinoCriado);
    }

    @Operation(
            summary = "Busca treino por Id."
    )
    @PutMapping("/{id}")
    public ResponseEntity<TreinoResponseDTO> updateTreino(
            @PathVariable Long id,
            @Valid @RequestBody TreinoUpdateDTO dto) {

        return ResponseEntity.ok(
                treinoService.updateTreino(id, dto)
        );
    }

    @Operation(
            summary = "Altera status de um treino existente."
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<TreinoResponseDTO> alterarStatus(
            @PathVariable Long id,
            @RequestParam Boolean ativo) {

        return ResponseEntity.ok(
                treinoService.alterarStatus(id, ativo)
        );
    }

    @Operation(
            summary = "Deleta treino por Id."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<TreinoResponseDTO> deleteTreino(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                treinoService.deleteTreino(id)
        );
    }
}