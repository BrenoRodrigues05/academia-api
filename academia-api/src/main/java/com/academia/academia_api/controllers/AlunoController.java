package com.academia.academia_api.controllers;

import com.academia.academia_api.DTOs.AlunoCreateDTO;
import com.academia.academia_api.DTOs.AlunoResponseDTO;
import com.academia.academia_api.DTOs.AlunoUpdateDTO;
import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.enums.SexoEnum;
import com.academia.academia_api.services.AlunoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping
    public ResponseEntity<List<AlunoResponseDTO>> listarAlunos() {
        List<AlunoResponseDTO> dtos = alunoService.listarAlunos()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> findById(@PathVariable Long id) {
        Aluno aluno = alunoService.fIndByid(id);
        return ResponseEntity.ok(convertToResponseDTO(aluno));
    }

    @GetMapping("/busca-nome")
    public ResponseEntity<List<AlunoResponseDTO>> findByNome(@RequestParam String nome) {
        List<AlunoResponseDTO> dtos = alunoService.findByNome(nome)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/busca-email")
    public ResponseEntity<AlunoResponseDTO> findByEmail(@RequestParam String email) {
        Aluno aluno = alunoService.findByEmail(email);
        return ResponseEntity.ok(convertToResponseDTO(aluno));
    }

    @GetMapping("/busca-sexo")
    public ResponseEntity<List<AlunoResponseDTO>> findBySexo(@RequestParam SexoEnum sexo) {
        List<AlunoResponseDTO> dtos = alunoService.findBySexo(sexo)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/busca-idade")
    public ResponseEntity<List<AlunoResponseDTO>> findByIdade(@RequestParam int idade) {
        List<AlunoResponseDTO> dtos = alunoService.findByIdade(idade)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<AlunoResponseDTO> addAluno(@Valid @RequestBody AlunoCreateDTO dto) {
        Aluno alunoEntity = new Aluno();
        alunoEntity.setNome(dto.getNome());
        alunoEntity.setEmail(dto.getEmail());
        alunoEntity.setDataNascimento(dto.getDataNascimento());
        alunoEntity.setTelefone(dto.getTelefone());
        alunoEntity.setSexo(dto.getSexo());

        Aluno novoAluno = alunoService.addAluno(alunoEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponseDTO(novoAluno));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> updateAluno(@PathVariable Long id, @Valid @RequestBody
    AlunoUpdateDTO dto) {
        Aluno alunoEntity = alunoService.fIndByid(id);

        alunoEntity.setNome(dto.getNome());
        alunoEntity.setTelefone(dto.getTelefone());
        alunoEntity.setSexo(dto.getSexo());

        Aluno alunoAtualizado = alunoService.updateAluno(alunoEntity);
        return ResponseEntity.ok(convertToResponseDTO(alunoAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> deleteAluno(@PathVariable Long id) {
        Aluno alunoDeletado = alunoService.deleteAluno(id);
        if (alunoDeletado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToResponseDTO(alunoDeletado));
    }

    private AlunoResponseDTO convertToResponseDTO(Aluno aluno) {
        AlunoResponseDTO dto = new AlunoResponseDTO();
        dto.setId(aluno.getId());
        dto.setNome(aluno.getNome());
        dto.setEmail(aluno.getEmail());
        dto.setDataNascimento(aluno.getDataNascimento());
        dto.setTelefone(aluno.getTelefone());
        dto.setSexo(aluno.getSexo());
        return dto;
    }
}