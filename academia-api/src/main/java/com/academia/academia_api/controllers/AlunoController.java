package com.academia.academia_api.controllers;

import com.academia.academia_api.DTOs.AlunoCreateDTO;
import com.academia.academia_api.DTOs.AlunoResponseDTO;
import com.academia.academia_api.DTOs.AlunoUpdateDTO;
import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.entity.enums.SexoEnum;
import com.academia.academia_api.services.AlunoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<AlunoResponseDTO>> findAll(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        return ResponseEntity.ok(
                alunoService.findAll(page, size)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(alunoService.findById(id));
    }

    @GetMapping("/busca-nome")
    public ResponseEntity<List<AlunoResponseDTO>> findByNome(@RequestParam String nome) {
        return ResponseEntity.ok(alunoService.findByNome(nome));
    }

    @GetMapping("/busca-email")
    public ResponseEntity<AlunoResponseDTO> findByEmail(@RequestParam String email) {
        return ResponseEntity.ok(alunoService.findByEmail(email));
    }

    @GetMapping("/busca-sexo")
    public ResponseEntity<PageResponseDTO<AlunoResponseDTO>> findBySexo(
            @RequestParam SexoEnum sexo,
            Pageable pageable) {

        return ResponseEntity.ok(
                alunoService.findBySexo(sexo, pageable.getPageNumber(), pageable.getPageSize())
        );
    }

    @GetMapping("/busca-idade")
    public ResponseEntity<List<AlunoResponseDTO>> findByIdade(@RequestParam int idade) {
        return ResponseEntity.ok(alunoService.findByIdade(idade));
    }

    @PostMapping
    public ResponseEntity<AlunoResponseDTO> addAluno(@Valid @RequestBody AlunoCreateDTO dto) {
        AlunoResponseDTO novoAluno = alunoService.addAluno(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAluno);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> updateAluno(@PathVariable Long id, @Valid @RequestBody AlunoUpdateDTO dto) {
        AlunoResponseDTO alunoAtualizado = alunoService.updateAluno(id, dto);
        return ResponseEntity.ok(alunoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> deleteAluno(@PathVariable Long id) {
        AlunoResponseDTO alunoDeletado = alunoService.deleteAluno(id);
        if (alunoDeletado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(alunoDeletado);
    }
}