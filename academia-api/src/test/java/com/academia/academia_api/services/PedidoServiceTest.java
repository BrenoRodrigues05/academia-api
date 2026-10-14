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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Pedidos (PedidoService)")
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private PedidoService pedidoService;

    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;
    private SecurityContext securityContext;
    private Authentication authentication;

    private Usuarios usuarioLogado;
    private Aluno aluno;
    private Produto produto;
    private Pedido pedido;
    private PedidoResponseDTO pedidoResponseDTO;

    @BeforeEach
    void setUp() {
        mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class);
        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);

        usuarioLogado = new Usuarios();
        usuarioLogado.setId(10L);

        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setUsuario(usuarioLogado);

        produto = new Produto();
        produto.setId(5L);
        produto.setNome("Whey Protein");
        produto.setPreco(new BigDecimal("100.00"));
        produto.setEstoque(10);

        pedido = new Pedido();
        pedido.setId(100L);
        pedido.setAluno(aluno);
        pedido.setValorTotal(new BigDecimal("100.00"));
        pedido.setStatus(StatusPedidoEnum.PENDENTE_PAGAMENTO);

        pedidoResponseDTO = new PedidoResponseDTO();
    }

    @AfterEach
    void tearDown() {
        mockedSecurityContextHolder.close();
    }

    private void mockUsuarioLogado(Usuarios usuario) {
        lenient().when(authentication.getPrincipal()).thenReturn(usuario);
    }

    @Nested
    @DisplayName("Cenários de Listagem Paginada (findAll)")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar página de pedidos com sucesso")
        void deveRetornarPaginaDePedidos() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Pedido> page = new PageImpl<>(List.of(pedido));

            when(pedidoRepository.findAll(pageable)).thenReturn(page);
            when(pedidoMapper.toResponseDTO(pedido)).thenReturn(pedidoResponseDTO);

            PageResponseDTO<PedidoResponseDTO> resultado = pedidoService.findAll(0, 10);

            assertNotNull(resultado);
            assertEquals(1, resultado.content().size());
            verify(pedidoRepository, times(1)).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Cenários de Busca por ID (getById)")
    class GetByIdTests {

        @Test
        @DisplayName("Deve retornar o pedido quando o ID for válido e existir")
        void deveRetornarPedidoPorId() {
            when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
            when(pedidoMapper.toResponseDTO(pedido)).thenReturn(pedidoResponseDTO);

            PedidoResponseDTO resultado = pedidoService.getById(100L);

            assertNotNull(resultado);
            verify(pedidoRepository, times(1)).findById(100L);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o ID for nulo ou menor/igual a zero")
        void deveLancarExcecaoParaIdInvalido() {
            assertThrows(BadRequestException.class, () -> pedidoService.getById(null));
            assertThrows(BadRequestException.class, () -> pedidoService.getById(0L));
            assertThrows(BadRequestException.class, () -> pedidoService.getById(-1L));

            verify(pedidoRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se o pedido não for encontrado")
        void deveLancarErroSePedidoNaoExistir() {
            when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> pedidoService.getById(99L)
            );

            assertEquals("Pedido não encontrado com ID: 99", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Cenários de Busca por Status (getByStatus)")
    class GetByStatusTests {

        @Test
        @DisplayName("Deve retornar lista de pedidos pelo status informado")
        void deveRetornarPedidosPorStatus() {
            when(pedidoRepository.findByStatus(StatusPagamentoEnum.PENDENTE))
                    .thenReturn(List.of(pedido));
            when(pedidoMapper.toResponseDTO(pedido)).thenReturn(pedidoResponseDTO);

            List<PedidoResponseDTO> resultado = pedidoService.getByStatus(StatusPagamentoEnum.PENDENTE);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(pedidoRepository, times(1)).findByStatus(StatusPagamentoEnum.PENDENTE);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o status for nulo")
        void deveLancarErroSeStatusForNulo() {
            assertThrows(BadRequestException.class, () -> pedidoService.getByStatus(null));
            verify(pedidoRepository, never()).findByStatus(any());
        }
    }

    @Nested
    @DisplayName("Cenários de Criação de Pedido (create)")
    class CreatePedidoTests {

        @Test
        @DisplayName("Deve criar pedido com sucesso e atualizar o estoque (Pagar na Retirada)")
        void deveCriarPedidoComSucessoRetirada() {
            mockUsuarioLogado(usuarioLogado);

            ItemCarrinhoDTO itemDto = new ItemCarrinhoDTO();
            itemDto.setProdutoId(5L);
            itemDto.setQuantidade(2);

            PedidoCreateDTO createDTO = new PedidoCreateDTO();
            createDTO.setItens(List.of(itemDto));
            createDTO.setMetodoPagamento(MetodoPagamentoLojaEnum.PAGAR_NA_RETIRADA);

            when(alunoRepository.findByUsuarioId(10L)).thenReturn(Optional.of(aluno));
            when(produtoRepository.findById(5L)).thenReturn(Optional.of(produto));
            when(produtoRepository.save(produto)).thenReturn(produto);
            when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
            when(pedidoMapper.toResponseDTO(pedido)).thenReturn(pedidoResponseDTO);

            PedidoResponseDTO resultado = pedidoService.create(createDTO);

            assertNotNull(resultado);
            assertEquals(8, produto.getEstoque()); // Estoque original 10 - 2 comprados
            verify(produtoRepository, times(1)).save(produto);
            verify(pedidoRepository, times(1)).save(any(Pedido.class));
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o carrinho estiver vazio ou nulo")
        void deveValidarCarrinhoVazio() {
            PedidoCreateDTO createDTO = new PedidoCreateDTO();
            createDTO.setItens(null);
            createDTO.setMetodoPagamento(MetodoPagamentoLojaEnum.PAGAR_NA_RETIRADA);

            BadRequestException ex = assertThrows(BadRequestException.class, () -> pedidoService.create(createDTO));
            assertEquals("O carrinho não pode estar vazio.", ex.getMessage());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o método de pagamento não for informado")
        void deveValidarMetodoPagamentoObrigatorio() {
            ItemCarrinhoDTO itemDto = new ItemCarrinhoDTO();
            itemDto.setProdutoId(5L);
            itemDto.setQuantidade(1);

            PedidoCreateDTO createDTO = new PedidoCreateDTO();
            createDTO.setItens(List.of(itemDto));
            createDTO.setMetodoPagamento(null);

            BadRequestException ex = assertThrows(BadRequestException.class, () -> pedidoService.create(createDTO));
            assertEquals("O método de pagamento da loja deve ser informado.", ex.getMessage());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se a quantidade do item for inválida")
        void deveValidarQuantidadeItemInvalida() {
            mockUsuarioLogado(usuarioLogado);

            ItemCarrinhoDTO itemDto = new ItemCarrinhoDTO();
            itemDto.setProdutoId(5L);
            itemDto.setQuantidade(0);

            PedidoCreateDTO createDTO = new PedidoCreateDTO();
            createDTO.setItens(List.of(itemDto));
            createDTO.setMetodoPagamento(MetodoPagamentoLojaEnum.PAGAR_NA_RETIRADA);

            when(alunoRepository.findByUsuarioId(10L)).thenReturn(Optional.of(aluno));

            BadRequestException ex = assertThrows(BadRequestException.class, () -> pedidoService.create(createDTO));
            assertEquals("A quantidade do item deve ser maior que zero.", ex.getMessage());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o estoque for insuficiente")
        void deveValidarEstoqueInsuficiente() {
            mockUsuarioLogado(usuarioLogado);

            ItemCarrinhoDTO itemDto = new ItemCarrinhoDTO();
            itemDto.setProdutoId(5L);
            itemDto.setQuantidade(15);

            PedidoCreateDTO createDTO = new PedidoCreateDTO();
            createDTO.setItens(List.of(itemDto));
            createDTO.setMetodoPagamento(MetodoPagamentoLojaEnum.PAGAR_NA_RETIRADA);

            when(alunoRepository.findByUsuarioId(10L)).thenReturn(Optional.of(aluno));
            when(produtoRepository.findById(5L)).thenReturn(Optional.of(produto));

            BadRequestException ex = assertThrows(BadRequestException.class, () -> pedidoService.create(createDTO));
            assertTrue(ex.getMessage().contains("Estoque insuficiente"));
            verify(produtoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Cenários de Listagem dos Pedidos do Aluno Logado (listarPedidosDoAlunoLogado)")
    class ListarPedidosDoAlunoLogadoTests {

        @Test
        @DisplayName("Deve listar os pedidos do aluno logado com sucesso")
        void deveListarPedidosDoAlunoLogado() {
            mockUsuarioLogado(usuarioLogado);

            when(alunoRepository.findByUsuarioId(10L)).thenReturn(Optional.of(aluno));
            when(pedidoRepository.findByAlunoId(1L)).thenReturn(List.of(pedido));
            when(pedidoMapper.toResponseDTO(pedido)).thenReturn(pedidoResponseDTO);

            List<PedidoResponseDTO> resultado = pedidoService.listarPedidosDoAlunoLogado();

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(pedidoRepository, times(1)).findByAlunoId(1L);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException se o aluno não estiver vinculado ao usuário logado")
        void deveLancarErroSeAlunoNaoEncontradoParaUsuario() {
            mockUsuarioLogado(usuarioLogado);

            when(alunoRepository.findByUsuarioId(10L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> pedidoService.listarPedidosDoAlunoLogado()
            );

            assertEquals("Aluno não encontrado para o usuário logado.", ex.getMessage());
        }
    }
}