package com.academia.academia_api.controllers;

import com.academia.academia_api.DTOs.AlunoCreateDTO;
import com.academia.academia_api.DTOs.AlunoResponseDTO;
import com.academia.academia_api.DTOs.AlunoUpdateDTO;
import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.entity.enums.SexoEnum;
import com.academia.academia_api.services.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Alunos.",
        description = "Gerenciamento dos alunos da academia."
)
@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @Operation(
            summary = "Listar alunos.",
            description = "Lista todos os alunos com paginação, filtros e ordenação."
    )
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

    @Operation(
            summary = "Buscar aluno por ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(alunoService.findById(id));
    }

    @Operation(
            summary = "Buscar aluno por Nome."
    )
    @GetMapping("/busca-nome")
    public ResponseEntity<List<AlunoResponseDTO>> findByNome(@RequestParam String nome) {
        return ResponseEntity.ok(alunoService.findByNome(nome));
    }

    @Operation(
            summary = "Buscar aluno por E-Mail."
    )
    @GetMapping("/busca-email")
    public ResponseEntity<AlunoResponseDTO> findByEmail(@RequestParam String email) {
        return ResponseEntity.ok(alunoService.findByEmail(email));
    }

    @Operation(
            summary = "Buscar aluno por Sexo."
    )
    @GetMapping("/busca-sexo")
    public ResponseEntity<PageResponseDTO<AlunoResponseDTO>> findBySexo(
            @RequestParam SexoEnum sexo,
            Pageable pageable) {

        return ResponseEntity.ok(
                alunoService.findBySexo(sexo, pageable.getPageNumber(), pageable.getPageSize())
        );
    }

    @Operation(
            summary = "Buscar aluno por Idade."
    )
    @GetMapping("/busca-idade")
    public ResponseEntity<List<AlunoResponseDTO>> findByIdade(@RequestParam int idade) {
        return ResponseEntity.ok(alunoService.findByIdade(idade));
    }

    @Operation(
            summary = "Cadastrar aluno."
    )
    @PostMapping
    public ResponseEntity<AlunoResponseDTO> addAluno(@Valid @RequestBody AlunoCreateDTO dto) {
        AlunoResponseDTO novoAluno = alunoService.addAluno(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAluno);
    }

    @Operation(
            summary = "Atualizar aluno."
    )
    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> updateAluno(@PathVariable Long id, @Valid @RequestBody AlunoUpdateDTO dto) {
        AlunoResponseDTO alunoAtualizado = alunoService.updateAluno(id, dto);
        return ResponseEntity.ok(alunoAtualizado);
    }

    @Operation(
            summary = "Alterar Status do aluno."
    )
    @PatchMapping("/{id}/ativo")
    public ResponseEntity<AlunoResponseDTO> alternarStatusAluno(@PathVariable Long id, boolean novoStatus){
        AlunoResponseDTO alunoInativo = alunoService.alternarStatusAluno(id, novoStatus);
        return ResponseEntity.ok(alunoInativo);
    }

    @Operation(
            summary = "Excluir aluno."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> deleteAluno(@PathVariable Long id) {
        AlunoResponseDTO alunoDeletado = alunoService.deleteAluno(id);
        if (alunoDeletado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(alunoDeletado);
    }
}