package com.academia.academia_api.controllers;

import com.academia.academia_api.DTOs.TreinoCreateDTO;
import com.academia.academia_api.DTOs.TreinoResponseDTO;
import com.academia.academia_api.DTOs.TreinoUpdateDTO;
import com.academia.academia_api.services.TreinoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treinos")
public class TreinoController {

    private final TreinoService treinoService;

    public TreinoController(TreinoService treinoService) {
        this.treinoService = treinoService;
    }

    @GetMapping
    public ResponseEntity<List<TreinoResponseDTO>> findAll() {
        return ResponseEntity.ok(treinoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreinoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(treinoService.findById(id));
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<TreinoResponseDTO>> findByAtivoTrue() {
        return ResponseEntity.ok(treinoService.findByAtivoTrue());
    }

    @GetMapping("/inativos")
    public ResponseEntity<List<TreinoResponseDTO>> findByAtivoFalse() {
        return ResponseEntity.ok(treinoService.findByAtivoFalse());
    }

    @GetMapping("/busca-nome")
    public ResponseEntity<List<TreinoResponseDTO>> findByNome(
            @RequestParam String nome) {

        return ResponseEntity.ok(treinoService.findByNome(nome));
    }

    @GetMapping("/personal/{personalId}")
    public ResponseEntity<List<TreinoResponseDTO>> findByPersonal(
            @PathVariable Long personalId) {

        return ResponseEntity.ok(treinoService.findByPersonal(personalId));
    }

    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<TreinoResponseDTO> findTreinoAtivoAluno(
            @PathVariable Long alunoId) {

        return ResponseEntity.ok(
                treinoService.findTreinoAtivoAluno(alunoId)
        );
    }

    @PostMapping
    public ResponseEntity<TreinoResponseDTO> addTreino(
            @Valid @RequestBody TreinoCreateDTO dto) {

        TreinoResponseDTO treinoCriado =
                treinoService.addTreino(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(treinoCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TreinoResponseDTO> updateTreino(
            @PathVariable Long id,
            @Valid @RequestBody TreinoUpdateDTO dto) {

        return ResponseEntity.ok(
                treinoService.updateTreino(id, dto)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TreinoResponseDTO> alterarStatus(
            @PathVariable Long id,
            @RequestParam Boolean ativo) {

        return ResponseEntity.ok(
                treinoService.alterarStatus(id, ativo)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TreinoResponseDTO> deleteTreino(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                treinoService.deleteTreino(id)
        );
    }
}