package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.DTOs.ProdutoCreateDTO;
import com.academia.academia_api.DTOs.ProdutoResponseDTO;
import com.academia.academia_api.DTOs.ProdutoUpdateDTO;
import com.academia.academia_api.entity.Produto;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.mappings.ProdutoMapper;
import com.academia.academia_api.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;

    public ProdutoService(ProdutoRepository produtoRepository, ProdutoMapper produtoMapper) {
        this.produtoRepository = produtoRepository;
        this.produtoMapper = produtoMapper;
    }

    public PageResponseDTO<ProdutoResponseDTO> findAll(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Produto> produtos = produtoRepository.findAll(pageable);

        return new PageResponseDTO<>(
                produtos.getContent().stream().map(produtoMapper::toResponseDTO).toList(),
                produtos.getNumber(),
                produtos.getSize(),
                produtos.getTotalElements(),
                produtos.getTotalPages()
        );
    }

    public ProdutoResponseDTO findByid (Long id){

        if(id == null || id <= 0){
            throw new BadRequestException("O ID informado é inválido ou nulo.");
        }

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

        return produtoMapper.toResponseDTO(produto);
    }

    public List<ProdutoResponseDTO> findByNome (String nome){

        if(nome == null){
            throw new BadRequestException("Nome vazio o inválido.");
        }

        List<Produto> produtos = produtoRepository.findByNomeContainingIgnoreCase(nome);

        if(produtos.isEmpty()){
            throw new ResourceNotFoundException("Nenhum produto encontrado com esse nome.");
        }

        return produtos.stream()
                .map(produtoMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public ProdutoResponseDTO addProduto (ProdutoCreateDTO produtoCreateDTO){

        if (produtoCreateDTO.getPreco() == null || produtoCreateDTO.getPreco().signum() <= 0) {
            throw new BadRequestException("O preço do produto deve ser maior que zero.");
        }
        if (produtoCreateDTO.getEstoque() == null || produtoCreateDTO.getEstoque() < 0) {
            throw new BadRequestException("O estoque inicial não pode ser negativo.");
        }

        Produto produto = produtoMapper.toEntity(produtoCreateDTO);

        Produto novoProduto = produtoRepository.save(produto);
        return produtoMapper.toResponseDTO(novoProduto);
    }

    @Transactional
    public ProdutoResponseDTO updateProduto (Long id, ProdutoUpdateDTO produtoUpdateDTO){

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + id));

        produtoMapper.updateEntityFromDTO(produtoUpdateDTO, produto);

        Produto atualizado = produtoRepository.save(produto);
        return produtoMapper.toResponseDTO(atualizado);
    }

    @Transactional
    public ProdutoResponseDTO deleteProduto (Long id){
        if (id == null) {
            throw new BadRequestException("O ID fornecido para exclusão não pode ser nulo.");
        }

        Produto produtoDeletado = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));

        produtoRepository.delete(produtoDeletado);
        return produtoMapper.toResponseDTO(produtoDeletado);
    }
}
