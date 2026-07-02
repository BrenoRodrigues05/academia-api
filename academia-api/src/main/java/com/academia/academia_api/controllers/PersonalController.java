package com.academia.academia_api.controllers;

import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.DTOs.PersonalCreateDTO;
import com.academia.academia_api.DTOs.PersonalResponseDTO;
import com.academia.academia_api.DTOs.PersonalUpdateDTO;
import com.academia.academia_api.services.PersonalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Personais.",
        description = "Gerenciamento dos personais da academia."
)
@RestController
@RequestMapping("/api/personais")
public class PersonalController {

    private final PersonalService personalService;

    public PersonalController(PersonalService personalService) {
        this.personalService = personalService;
    }

    @Operation(
            summary = "Listar Personais.",
            description = "Lista todos os personais com paginação, filtros e ordenação."
    )
    @GetMapping
    public ResponseEntity<PageResponseDTO<PersonalResponseDTO>> findAll(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {
        return ResponseEntity.ok(personalService.findAll(page, size));
    }

    @Operation(
            summary = "Buscar personal por ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<PersonalResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(personalService.findById(id));
    }

    @Operation(
            summary = "Buscar personal por E-mail."
    )
    @GetMapping("/busca-email")
    public ResponseEntity<PersonalResponseDTO> findByEmail(@RequestParam String email) {
        return ResponseEntity.ok(personalService.findByEmail(email));
    }

    @Operation(
            summary = "Buscar personal por Cref."
    )
    @GetMapping("/busca-cref")
    public ResponseEntity<PersonalResponseDTO> findByCref(@RequestParam String cref) {
        return ResponseEntity.ok(personalService.findByCref(cref));
    }

    @Operation(
            summary = "Buscar personal por Nome."
    )
    @GetMapping("/busca-nome")
    public ResponseEntity<List<PersonalResponseDTO>> findByNome(@RequestParam String nome) {
        return ResponseEntity.ok(personalService.findByNome(nome));
    }

    @Operation(
            summary = "Buscar personais Ativos."
    )
    @GetMapping("/ativos")
    public ResponseEntity<List<PersonalResponseDTO>> findByAtivoTrue() {
        return ResponseEntity.ok(personalService.findByAtivoTrue());
    }

    @Operation(
            summary = "Buscar personais Inativos."
    )
    @GetMapping("/inativos")
    public ResponseEntity<List<PersonalResponseDTO>> findByAtivoFalse() {
        return ResponseEntity.ok(personalService.findByAtivoFalse());
    }

    @Operation(
            summary = "Buscar cria um novo Personal."
    )
    @PostMapping
    public ResponseEntity<PersonalResponseDTO> addPersonal(
            @Valid @RequestBody PersonalCreateDTO dto) {

        PersonalResponseDTO novoPersonal = personalService.addPersonal(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(novoPersonal);
    }

    @Operation(
            summary = "Edita um Personal existente."
    )
    @PutMapping("/{id}")
    public ResponseEntity<PersonalResponseDTO> updatePersonal(
            @PathVariable Long id,
            @Valid @RequestBody PersonalUpdateDTO dto) {

        return ResponseEntity.ok(
                personalService.updatePersonal(id, dto)
        );
    }

    @Operation(
            summary = "Alterar disponibilidade do Personal.",
            description = "Altera par Ativo ou Inativo no sistema."
    )
    @PatchMapping("/{id}/ativo")
    public ResponseEntity<PersonalResponseDTO> atualizarAtivo(
            @PathVariable Long id,
            @RequestParam boolean ativo) {

        return ResponseEntity.ok(
                personalService.atualizarAtivoPersonal(id, ativo)
        );
    }

    @Operation(
            summary = "Deletar personal por ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<PersonalResponseDTO> deletePersonal(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                personalService.deletePersonal(id)
        );
    }
}