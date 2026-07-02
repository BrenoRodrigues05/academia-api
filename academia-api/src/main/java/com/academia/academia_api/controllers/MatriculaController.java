package com.academia.academia_api.controllers;
import com.academia.academia_api.DTOs.MatriculaCreateDTO;
import com.academia.academia_api.DTOs.MatriculaResponseDTO;
import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.services.MatriculaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Matriculas.",
        description = "Gerenciamento das Matriculas da academia."
)
@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @Operation(
            summary = "Listar todas as Matriculas.",
            description = "Lista todas as Matriculas com paginação, filtros e ordenação."
    )
   @GetMapping
    public ResponseEntity<PageResponseDTO<MatriculaResponseDTO>> matriculas(
           @RequestParam(defaultValue = "0")
           int page,

           @RequestParam(defaultValue = "10")
           int size
   ) {
        return ResponseEntity.ok(matriculaService.listarMatriculas(page, size));
   }

    @Operation(
            summary = "Buscar Matriculas por Id."
    )
    @GetMapping("/{id}")
    public ResponseEntity<MatriculaResponseDTO> getMatricula(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.findById(id));
    }

    @Operation(
            summary = "Cria uma nova Matricula."
    )
    @PostMapping
    public ResponseEntity<MatriculaResponseDTO> criarMatricula(
            @Valid @RequestBody MatriculaCreateDTO dto,
            @RequestParam Long idAluno,
            @RequestParam Long idPlano) {

        MatriculaResponseDTO novaMatricula =
                matriculaService.criarMatricula(dto, idAluno, idPlano);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(novaMatricula);
    }

    @Operation(
            summary = "Desativa uma Matricula ativa por Id."
    )
    @PatchMapping("/{id}/desativar")
    public ResponseEntity<MatriculaResponseDTO> deletarMatricula(@PathVariable Long id){
        MatriculaResponseDTO matriculaDesativada = matriculaService.desativarMatricula(id);
        return ResponseEntity.ok(matriculaDesativada);
    }
}
