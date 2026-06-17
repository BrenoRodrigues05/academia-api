package com.academia.academia_api.controllers;
import com.academia.academia_api.DTOs.MatriculaCreateDTO;
import com.academia.academia_api.DTOs.MatriculaResponseDTO;
import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.services.MatriculaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

   @GetMapping
    public ResponseEntity<PageResponseDTO<MatriculaResponseDTO>> matriculas(
           @RequestParam(defaultValue = "0")
           int page,

           @RequestParam(defaultValue = "10")
           int size
   ) {
        return ResponseEntity.ok(matriculaService.listarMatriculas(page, size));
   }

    @GetMapping("/{id}")
    public ResponseEntity<MatriculaResponseDTO> getMatricula(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.findById(id));
    }

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

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<MatriculaResponseDTO> deletarMatricula(@PathVariable Long id){
        MatriculaResponseDTO matriculaDesativada = matriculaService.desativarMatricula(id);
        return ResponseEntity.ok(matriculaDesativada);
    }
}
