package com.academia.academia_api.controllers;

import com.academia.academia_api.DTOs.PersonalCreateDTO;
import com.academia.academia_api.DTOs.PersonalResponseDTO;
import com.academia.academia_api.DTOs.PersonalUpdateDTO;
import com.academia.academia_api.services.PersonalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personais")
public class PersonalController {

    private final PersonalService personalService;

    public PersonalController(PersonalService personalService) {
        this.personalService = personalService;
    }

    @GetMapping
    public ResponseEntity<List<PersonalResponseDTO>> findAll() {
        return ResponseEntity.ok(personalService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonalResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(personalService.findById(id));
    }

    @GetMapping("/busca-email")
    public ResponseEntity<PersonalResponseDTO> findByEmail(@RequestParam String email) {
        return ResponseEntity.ok(personalService.findByEmail(email));
    }

    @GetMapping("/busca-cref")
    public ResponseEntity<PersonalResponseDTO> findByCref(@RequestParam String cref) {
        return ResponseEntity.ok(personalService.findByCref(cref));
    }

    @GetMapping("/busca-nome")
    public ResponseEntity<List<PersonalResponseDTO>> findByNome(@RequestParam String nome) {
        return ResponseEntity.ok(personalService.findByNome(nome));
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<PersonalResponseDTO>> findByAtivoTrue() {
        return ResponseEntity.ok(personalService.findByAtivoTrue());
    }

    @GetMapping("/inativos")
    public ResponseEntity<List<PersonalResponseDTO>> findByAtivoFalse() {
        return ResponseEntity.ok(personalService.findByAtivoFalse());
    }

    @PostMapping
    public ResponseEntity<PersonalResponseDTO> addPersonal(
            @Valid @RequestBody PersonalCreateDTO dto) {

        PersonalResponseDTO novoPersonal = personalService.addPersonal(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(novoPersonal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonalResponseDTO> updatePersonal(
            @PathVariable Long id,
            @Valid @RequestBody PersonalUpdateDTO dto) {

        return ResponseEntity.ok(
                personalService.updatePersonal(id, dto)
        );
    }

    @PatchMapping("/{id}/ativo")
    public ResponseEntity<PersonalResponseDTO> atualizarAtivo(
            @PathVariable Long id,
            @RequestParam boolean ativo) {

        return ResponseEntity.ok(
                personalService.atualizarAtivoPersonal(id, ativo)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PersonalResponseDTO> deletePersonal(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                personalService.deletePersonal(id)
        );
    }
}