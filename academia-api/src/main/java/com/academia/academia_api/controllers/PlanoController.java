package com.academia.academia_api.controllers;
import com.academia.academia_api.DTOs.PlanoCreateDTO;
import com.academia.academia_api.DTOs.PlanoResponseDTO;
import com.academia.academia_api.DTOs.PlanoUpdateDTO;
import com.academia.academia_api.entity.enums.TipoPlano;
import com.academia.academia_api.services.PlanoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(
        name = "Planos.",
        description = "Gerenciamento dos planos da academia."
)
@RestController
@RequestMapping("/api/planos")
public class PlanoController {

    private final PlanoService planoService;

    public PlanoController(PlanoService planoService) {
        this.planoService = planoService;
    }

    @Operation(
            summary = "Listar Planos.",
            description = "Lista todos os planos com paginação, filtros e ordenação."
    )
    @GetMapping
    public ResponseEntity<List<PlanoResponseDTO>> listarPlanos() {
        return ResponseEntity.ok(planoService.findAll());
    }

    @Operation(
            summary = "Buscar plano por ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<PlanoResponseDTO> buscaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(planoService.findById(id));
    }

    @Operation(
            summary = "Buscar plano por Nome."
    )
    @GetMapping("/busca-nome")
    public ResponseEntity<PlanoResponseDTO> buscaPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(planoService.BuscarPeloNome(nome));
    }

    @Operation(
            summary = "Buscar plano por valor."
    )
    @GetMapping("/busca-valor")
    public ResponseEntity<PlanoResponseDTO> buscaPorValor(@RequestParam BigDecimal valor) {
        return ResponseEntity.ok(planoService.BuscaPeloValor(valor));
    }

    @Operation(
            summary = "Buscar plano por descrição."
    )
    @GetMapping("/busca-descricao")
    public ResponseEntity<PlanoResponseDTO> buscaPorDescricao(@RequestParam String descricao) {
        return ResponseEntity.ok(planoService.BuscarPeloDescricao(descricao));
    }

    @Operation(
            summary = "Buscar plano por tipo.",
            description = "Busca planos por tipo: MENSAL, TRIMESTRAL, ANUAL."
    )
    @GetMapping("/busca-tipo")
    public  ResponseEntity<PlanoResponseDTO> buscaPorTipo(@RequestParam TipoPlano tipo) {
        return ResponseEntity.ok(planoService.BuscarPeloTipo(tipo));
    }

    @Operation(
            summary = "Cria um novo plano."
    )
    @PostMapping
    public ResponseEntity<PlanoResponseDTO> criarPlano(
            @RequestBody @Valid PlanoCreateDTO planoCreateDTO) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(planoService.criarPlano(planoCreateDTO));
    }

    @Operation(
            summary = "Editar plano por Id."
    )
    @PutMapping("/{id}")
    public ResponseEntity<PlanoResponseDTO> atualizarPlano(
            @PathVariable Long id,
            @RequestBody PlanoUpdateDTO dto){

        return ResponseEntity.ok(
                planoService.atualizarPlano(id, dto)
        );
    }

    @Operation(
            summary = "Deletar plano por Id."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<PlanoResponseDTO> deletarPlano(@PathVariable Long id){
        return ResponseEntity.ok(planoService.deletarPlano(id));
    }
}

