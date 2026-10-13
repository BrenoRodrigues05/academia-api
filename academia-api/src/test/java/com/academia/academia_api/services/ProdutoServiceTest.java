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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Produtos (ProdutoService)")
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ProdutoMapper produtoMapper;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;
    private ProdutoResponseDTO produtoResponseDTO;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Creatina Monohidratada");
        produto.setPreco(new BigDecimal("80.00"));
        produto.setEstoque(50);

        produtoResponseDTO = new ProdutoResponseDTO();
    }

    @Nested
    @DisplayName("Cenários de Listagem Paginada (findAll)")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar uma página de produtos com sucesso")
        void deveRetornarPaginaDeProdutos() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Produto> paginaProdutos = new PageImpl<>(List.of(produto));

            when(produtoRepository.findAll(pageable)).thenReturn(paginaProdutos);
            when(produtoMapper.toResponseDTO(produto)).thenReturn(produtoResponseDTO);

            PageResponseDTO<ProdutoResponseDTO> resultado = produtoService.findAll(0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.content().size());
            verify(produtoRepository, times(1)).findAll(pageable);
            verify(produtoMapper, times(1)).toResponseDTO(produto);
        }
    }

    @Nested
    @DisplayName("Cenários de Busca por ID (findByid)")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar o produto quando o ID for válido e existir")
        void deveRetornarProdutoPorId() {
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
            when(produtoMapper.toResponseDTO(produto)).thenReturn(produtoResponseDTO);

            ProdutoResponseDTO resultado = produtoService.findByid(1L);

            assertNotNull(resultado);
            verify(produtoRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o ID for nulo ou menor/igual a zero")
        void deveLancarExcecaoParaIdInvalido() {
            assertThrows(BadRequestException.class, () -> produtoService.findByid(null));
            assertThrows(BadRequestException.class, () -> produtoService.findByid(0L));
            assertThrows(BadRequestException.class, () -> produtoService.findByid(-5L));

            verify(produtoRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se o produto não for encontrado")
        void deveLancarExcecaoSeProdutoNaoExistir() {
            when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> produtoService.findByid(99L)
            );

            assertEquals("Produto não encontrado com ID: 99", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Cenários de Busca por Nome (findByNome)")
    class FindByNomeTests {

        @Test
        @DisplayName("Deve retornar lista de produtos correspondentes ao nome")
        void deveRetornarProdutosPorNome() {
            when(produtoRepository.findByNomeContainingIgnoreCase("Creatina"))
                    .thenReturn(List.of(produto));
            when(produtoMapper.toResponseDTO(produto)).thenReturn(produtoResponseDTO);

            List<ProdutoResponseDTO> resultado = produtoService.findByNome("Creatina");

            assertNotNull(resultado);
            assertFalse(resultado.isEmpty());
            assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o nome for nulo")
        void deveLancarExcecaoSeNomeForNulo() {
            assertThrows(BadRequestException.class, () -> produtoService.findByNome(null));
            verify(produtoRepository, never()).findByNomeContainingIgnoreCase(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se nenhum produto for encontrado")
        void deveLancarExcecaoSeListaVazia() {
            when(produtoRepository.findByNomeContainingIgnoreCase("Inexistente"))
                    .thenReturn(List.of());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> produtoService.findByNome("Inexistente")
            );

            assertEquals("Nenhum produto encontrado com esse nome.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Cenários de Adição de Produto (addProduto)")
    class AddProdutoTests {

        @Test
        @DisplayName("Deve adicionar um produto com sucesso quando os dados forem válidos")
        void deveAdicionarProdutoComSucesso() {
            ProdutoCreateDTO dto = new ProdutoCreateDTO();
            dto.setPreco(new BigDecimal("50.00"));
            dto.setEstoque(10);

            when(produtoMapper.toEntity(dto)).thenReturn(produto);
            when(produtoRepository.save(produto)).thenReturn(produto);
            when(produtoMapper.toResponseDTO(produto)).thenReturn(produtoResponseDTO);

            ProdutoResponseDTO resultado = produtoService.addProduto(dto);

            assertNotNull(resultado);
            verify(produtoRepository, times(1)).save(produto);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o preço for nulo ou menor/igual a zero")
        void deveValidarPrecoInvalido() {
            ProdutoCreateDTO dtoPrecoZero = new ProdutoCreateDTO();
            dtoPrecoZero.setPreco(BigDecimal.ZERO);
            dtoPrecoZero.setEstoque(10);

            BadRequestException exZero = assertThrows(
                    BadRequestException.class,
                    () -> produtoService.addProduto(dtoPrecoZero)
            );
            assertEquals("O preço do produto deve ser maior que zero.", exZero.getMessage());

            ProdutoCreateDTO dtoPrecoNulo = new ProdutoCreateDTO();
            dtoPrecoNulo.setPreco(null);
            dtoPrecoNulo.setEstoque(10);

            assertThrows(BadRequestException.class, () -> produtoService.addProduto(dtoPrecoNulo));
            verify(produtoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o estoque for negativo")
        void deveValidarEstoqueNegativo() {
            ProdutoCreateDTO dto = new ProdutoCreateDTO();
            dto.setPreco(new BigDecimal("50.00"));
            dto.setEstoque(-1);

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> produtoService.addProduto(dto)
            );

            assertEquals("O estoque inicial não pode ser negativo.", exception.getMessage());
            verify(produtoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Cenários de Atualização de Produto (updateProduto)")
    class UpdateProdutoTests {

        @Test
        @DisplayName("Deve atualizar o produto com sucesso quando o ID existir")
        void deveAtualizarProdutoComSucesso() {
            ProdutoUpdateDTO dto = new ProdutoUpdateDTO();

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
            doNothing().when(produtoMapper).updateEntityFromDTO(dto, produto);
            when(produtoRepository.save(produto)).thenReturn(produto);
            when(produtoMapper.toResponseDTO(produto)).thenReturn(produtoResponseDTO);

            ProdutoResponseDTO resultado = produtoService.updateProduto(1L, dto);

            assertNotNull(resultado);
            verify(produtoRepository, times(1)).findById(1L);
            verify(produtoMapper, times(1)).updateEntityFromDTO(dto, produto);
            verify(produtoRepository, times(1)).save(produto);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se o produto a atualizar não existir")
        void deveLancarErroSeProdutoNaoExistirNaAtualizacao() {
            ProdutoUpdateDTO dto = new ProdutoUpdateDTO();
            when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> produtoService.updateProduto(99L, dto)
            );

            assertEquals("Produto não encontrado com o ID: 99", exception.getMessage());
            verify(produtoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Cenários de Exclusão de Produto (deleteProduto)")
    class DeleteProdutoTests {

        @Test
        @DisplayName("Deve deletar o produto com sucesso quando o ID for válido e existir")
        void deveDeletarProdutoComSucesso() {
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
            doNothing().when(produtoRepository).delete(produto);
            when(produtoMapper.toResponseDTO(produto)).thenReturn(produtoResponseDTO);

            ProdutoResponseDTO resultado = produtoService.deleteProduto(1L);

            assertNotNull(resultado);
            verify(produtoRepository, times(1)).findById(1L);
            verify(produtoRepository, times(1)).delete(produto);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o ID fornecido para exclusão for nulo")
        void deveLancarErroSeIdExclusaoForNulo() {
            assertThrows(BadRequestException.class, () -> produtoService.deleteProduto(null));
            verify(produtoRepository, never()).findById(any());
            verify(produtoRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se o produto não for encontrado para exclusão")
        void deveLancarErroSeProdutoNaoExistirNaExclusao() {
            when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> produtoService.deleteProduto(99L)
            );

            assertEquals("Produto não encontrado.", exception.getMessage());
            verify(produtoRepository, never()).delete(any());
        }
    }
}