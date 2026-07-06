package com.academia.academia_api.controllers;

import com.academia.academia_api.DTOs.ExecucaoTreinoResponseDTO;
import com.academia.academia_api.services.ExecucaoTreinoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Execuções.",
        description = "Gerenciamento das execuções dos treinos."
)
@RestController
@RequestMapping("/api/execucoes")
public class ExecucoesTreinoController {

    private final ExecucaoTreinoService execucaoTreinoService;

    public ExecucoesTreinoController(ExecucaoTreinoService execucaoTreinoService) {
        this.execucaoTreinoService = execucaoTreinoService;
    }

    @Operation(
            summary = "Marca o inicio do treino ao aluno."
    )
    @PostMapping("/iniciar/{treinoId}")
    public ResponseEntity<ExecucaoTreinoResponseDTO> iniciarTreino(@PathVariable Long treinoId) {
        ExecucaoTreinoResponseDTO response = execucaoTreinoService.iniciarTreino(treinoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
