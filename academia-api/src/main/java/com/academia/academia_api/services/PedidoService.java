package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.ItemCarrinhoDTO;
import com.academia.academia_api.DTOs.PageResponseDTO;
import com.academia.academia_api.DTOs.PedidoCreateDTO;
import com.academia.academia_api.DTOs.PedidoResponseDTO;
import com.academia.academia_api.entity.*;
import com.academia.academia_api.entity.enums.MetodoPagamentoLojaEnum;
import com.academia.academia_api.entity.enums.StatusPagamentoEnum;
import com.academia.academia_api.entity.enums.StatusPedidoEnum;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.mappings.PedidoMapper;
import com.academia.academia_api.repository.AlunoRepository;
import com.academia.academia_api.repository.PedidoRepository;
import com.academia.academia_api.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final AlunoRepository alunoRepository;
    private final PedidoMapper pedidoMapper;

    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository,
                         AlunoRepository alunoRepository, PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.alunoRepository = alunoRepository;
        this.pedidoMapper = pedidoMapper;
    }

    public PageResponseDTO<PedidoResponseDTO> findAll(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Pedido> pedidos = pedidoRepository.findAll(pageable);

        return new PageResponseDTO<>(
            pedidos.getContent().stream().map(pedidoMapper::toResponseDTO).toList(),
            pedidos.getNumber(),
            pedidos.getSize(),
            pedidos.getTotalElements(),
            pedidos.getTotalPages()
        );
    }

    public PedidoResponseDTO getById(Long id){

        if(id == null || id <= 0){
            throw new BadRequestException("O ID informado é inválido ou nulo.");
        }

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + id));

        return  pedidoMapper.toResponseDTO(pedido);
    }

    public List<PedidoResponseDTO> getByStatus(StatusPagamentoEnum status){
        if (status == null) {
            throw new BadRequestException("Status é inválido ou nulo.");
        }
        List<Pedido> pedidos = pedidoRepository.findByStatus(status);
        return pedidos.stream()
                .map(pedidoMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public PedidoResponseDTO create(PedidoCreateDTO pedidoCreateDTO){
        if (pedidoCreateDTO.getItens() == null || pedidoCreateDTO.getItens().isEmpty()) {
            throw new BadRequestException("O carrinho não pode estar vazio.");
        }

        if (pedidoCreateDTO.getMetodoPagamento() == null) {
            throw new BadRequestException("O método de pagamento da loja deve ser informado.");
        }

        Usuarios usuarioLogado = getUsuarioLogado();
        Aluno aluno = alunoRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado para o usuário logado."));

        Pedido pedido = new Pedido();
        pedido.setAluno(aluno);
        pedido.setMetodoPagamento(pedidoCreateDTO.getMetodoPagamento());

        if (pedidoCreateDTO.getMetodoPagamento() == MetodoPagamentoLojaEnum.PAGAR_NA_RETIRADA) {
            pedido.setStatus(StatusPedidoEnum.RESERVADO_RETIRADA);
        } else {
            pedido.setStatus(StatusPedidoEnum.PENDENTE_PAGAMENTO);
        }

        BigDecimal valorTotal = BigDecimal.ZERO;
        List<ItemPedido> itensPedido = new ArrayList<>();

        for (ItemCarrinhoDTO itemDto : pedidoCreateDTO.getItens()) {
            if (itemDto.getQuantidade() == null || itemDto.getQuantidade() <= 0) {
                throw new BadRequestException("A quantidade do item deve ser maior que zero.");
            }

            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + itemDto.getProdutoId()));

            if (produto.getEstoque() < itemDto.getQuantidade()) {
                throw new BadRequestException("Estoque insuficiente para o produto: " + produto.getNome() + ". Disponível: " + produto.getEstoque());
            }

            produto.setEstoque(produto.getEstoque() - itemDto.getQuantidade());
            produtoRepository.save(produto);

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemDto.getQuantidade());
            itemPedido.setPrecoUnitario(produto.getPreco());

            itensPedido.add(itemPedido);

            BigDecimal subtotal = produto.getPreco().multiply(BigDecimal.valueOf(itemDto.getQuantidade()));
            valorTotal = valorTotal.add(subtotal);
        }

        pedido.setItens(itensPedido);
        pedido.setValorTotal(valorTotal);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        return pedidoMapper.toResponseDTO(pedidoSalvo);
    }

    public List<PedidoResponseDTO> listarPedidosDoAlunoLogado() {
        Usuarios usuarioLogado = getUsuarioLogado();
        Aluno aluno = alunoRepository.findByUsuarioId(usuarioLogado.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado para o usuário logado."));

        return pedidoRepository.findByAlunoId(aluno.getId()).stream()
                .map(pedidoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    private Usuarios getUsuarioLogado() {
        return (Usuarios) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
