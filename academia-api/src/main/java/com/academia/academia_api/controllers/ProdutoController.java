package com.academia.academia_api.controllers;

import com.academia.academia_api.DTOs.*;
import com.academia.academia_api.services.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Produtos.",
        description = "Gerenciamento dos Produtos da loja da academia."
)
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @Operation(
            summary = "Listar Produtos.",
            description = "Lista todos os Produtos da loja com paginação."
    )
    @GetMapping
    public ResponseEntity<PageResponseDTO<ProdutoResponseDTO>> findAll(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {
        return ResponseEntity.ok(produtoService.findAll(page, size));
    }

    @Operation(
            summary = "Buscar Produto por Id."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        ProdutoResponseDTO produto = produtoService.findByid(id);
        return ResponseEntity.ok(produto);
    }

    @Operation(
            summary = "Buscar Produto por Nome."
    )
    @GetMapping("/busca-nome")
    public ResponseEntity<ProdutoResponseDTO> buscarPorNome(String nome){
        List<ProdutoResponseDTO> produtos = produtoService.findByNome(nome);
        return ResponseEntity.ok(produtos.get(0));
    }

    @Operation(
            summary = "Criar um novo Produto."
    )
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criarProduto(@RequestBody @Valid ProdutoCreateDTO dto) {
        ProdutoResponseDTO novoProduto = produtoService.addProduto(dto);
        return ResponseEntity.ok(novoProduto);
    }

    @Operation(
            summary = "Editar um Produto."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(@PathVariable Long id, @RequestBody
    @Valid ProdutoUpdateDTO dto) {
        ProdutoResponseDTO produtoAtualizado = produtoService.updateProduto(id, dto);
        return ResponseEntity.ok(produtoAtualizado);
    }

    @Operation(
            summary = "Deletar um Produto pelo Id."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        produtoService.deleteProduto(id);
        return ResponseEntity.noContent().build();
    }
}
