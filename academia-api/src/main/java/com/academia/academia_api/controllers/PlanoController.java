package com.academia.academia_api.controllers;
import com.academia.academia_api.DTOs.PlanoCreateDTO;
import com.academia.academia_api.DTOs.PlanoResponseDTO;
import com.academia.academia_api.DTOs.PlanoUpdateDTO;
import com.academia.academia_api.entity.enums.TipoPlano;
import com.academia.academia_api.services.PlanoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/planos")
public class PlanoController {

    private final PlanoService planoService;

    public PlanoController(PlanoService planoService) {
        this.planoService = planoService;
    }

    @GetMapping
    public ResponseEntity<List<PlanoResponseDTO>> listarPlanos() {
        return ResponseEntity.ok(planoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanoResponseDTO> buscaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(planoService.findById(id));
    }

    @GetMapping("/busca-nome")
    public ResponseEntity<PlanoResponseDTO> buscaPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(planoService.BuscarPeloNome(nome));
    }

    @GetMapping("/busca-valor")
    public ResponseEntity<PlanoResponseDTO> buscaPorValor(@RequestParam BigDecimal valor) {
        return ResponseEntity.ok(planoService.BuscaPeloValor(valor));
    }

    @GetMapping("/busca-descricao")
    public ResponseEntity<PlanoResponseDTO> buscaPorDescricao(@RequestParam String descricao) {
        return ResponseEntity.ok(planoService.BuscarPeloDescricao(descricao));
    }

    @GetMapping("/busca-tipo")
    public  ResponseEntity<PlanoResponseDTO> buscaPorTipo(@RequestParam TipoPlano tipo) {
        return ResponseEntity.ok(planoService.BuscarPeloTipo(tipo));
    }

    @PostMapping
    public ResponseEntity<PlanoResponseDTO> criarPlano(
            @RequestBody @Valid PlanoCreateDTO planoCreateDTO) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(planoService.criarPlano(planoCreateDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanoResponseDTO> atualizarPlano(
            @PathVariable Long id,
            @RequestBody PlanoUpdateDTO dto){

        return ResponseEntity.ok(
                planoService.atualizarPlano(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PlanoResponseDTO> deletarPlano(@PathVariable Long id){
        return ResponseEntity.ok(planoService.deletarPlano(id));
    }
}

