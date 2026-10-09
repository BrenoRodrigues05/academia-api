package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.PixCobrancaResponse;
import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.Matricula;
import com.academia.academia_api.entity.Pagamento;
import com.academia.academia_api.entity.Plano;
import com.academia.academia_api.entity.Usuarios;
import com.academia.academia_api.entity.enums.MetodoPagamentoEnum;
import com.academia.academia_api.entity.enums.StatusPagamentoEnum;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.gateway.PixGateway;
import com.academia.academia_api.repository.MatriculaRepositoy;
import com.academia.academia_api.repository.PagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Pagamentos (PagamentoService)")
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private MatriculaRepositoy matriculaRepositoy;

    @Mock
    private PixGateway pixGateway;

    @InjectMocks
    private PagamentoService pagamentoService;

    private Matricula matriculaMock;
    private Plano planoMock;
    private Aluno alunoMock;
    private Usuarios usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuarios();
        usuarioMock.setId(1L);
        usuarioMock.setLogin("aluno.teste");

        alunoMock = new Aluno();
        alunoMock.setId(1L);
        alunoMock.setEmail("aluno@teste.com");
        alunoMock.setUsuario(usuarioMock);

        planoMock = new Plano();
        planoMock.setId(1L);
        planoMock.setNome("Plano Mensal");
        planoMock.setValor(new BigDecimal("100.00"));

        matriculaMock = new Matricula();
        matriculaMock.setMatricula(10L);
        matriculaMock.setAluno(alunoMock);
        matriculaMock.setPlano(planoMock);
        matriculaMock.setAtiva(false);
    }

    @Nested
    @DisplayName("Testes do método criarPagamentoPix")
    class CriarPagamentoPixTests {

        @Test
        @DisplayName("Deve criar cobrança PIX e retornar pagamento com dados do gateway com sucesso")
        void deveCriarPagamentoPixComSucesso() {

            PixCobrancaResponse pixResponse = new PixCobrancaResponse(
                    "transacao-gw-123",
                    "000201...copiaECola",
                    "data:image/png;base64,iVBORw0KGgo...",
                    new BigDecimal("100.00"),
                    LocalDateTime.now().plusDays(1)
            );

            when(pagamentoRepository.save(any(Pagamento.class)))
                    .thenAnswer(invocation -> {
                        Pagamento p = invocation.getArgument(0);
                        if (p.getId() == null) {
                            p.setId(100L);
                        }
                        return p;
                    });

            when(pixGateway.gerarCobrancaPix(
                    eq(100L),
                    eq(new BigDecimal("100.00")),
                    eq("Matrícula Plano: Plano Mensal"),
                    eq("aluno@teste.com")
            )).thenReturn(pixResponse);

            Pagamento resultado = pagamentoService.criarPagamentoPix(matriculaMock);

            assertNotNull(resultado);
            assertEquals(100L, resultado.getId());
            assertEquals(StatusPagamentoEnum.PENDENTE, resultado.getStatus());
            assertEquals(MetodoPagamentoEnum.PIX, resultado.getMetodoPagamento());
            assertEquals("transacao-gw-123", resultado.getGatewayId());
            assertEquals("000201...copiaECola", resultado.getCodigoPix());
            assertEquals("data:image/png;base64,iVBORw0KGgo...", resultado.getQrCodeBase64());

            verify(pagamentoRepository, times(2)).save(any(Pagamento.class));
            verify(pixGateway, times(1)).gerarCobrancaPix(
                    eq(100L),
                    eq(new BigDecimal("100.00")),
                    eq("Matrícula Plano: Plano Mensal"),
                    eq("aluno@teste.com")
            );
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o e-mail do aluno for nulo ou em branco")
        void deveLancarExcecaoQuandoEmailAlunoForInvalido() {
            alunoMock.setEmail(null);

            when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(invocation -> {
                Pagamento p = invocation.getArgument(0);
                p.setId(100L);
                return p;
            });

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> pagamentoService.criarPagamentoPix(matriculaMock)
            );

            assertEquals("E-mail do aluno é obrigatório para gerar o Pix.", exception.getMessage());
            verify(pixGateway, never()).gerarCobrancaPix(any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Testes do método processarNotificacaoMercadoPago")
    class ProcessarNotificacaoMercadoPagoTests {

        @Test
        @DisplayName("Deve atualizar pagamento para PAGO e ativar matrícula com sucesso ao receber notificação")
        void deveProcessarNotificacaoComSucesso() {
            Pagamento pagamentoPendente = new Pagamento();
            pagamentoPendente.setId(100L);
            pagamentoPendente.setGatewayId("transacao-gw-123");
            pagamentoPendente.setStatus(StatusPagamentoEnum.PENDENTE);
            pagamentoPendente.setMatricula(matriculaMock);

            when(pagamentoRepository.findByGatewayId("transacao-gw-123"))
                    .thenReturn(Optional.of(pagamentoPendente));

            pagamentoService.processarNotificacaoMercadoPago("transacao-gw-123");

            assertEquals(StatusPagamentoEnum.PAGO, pagamentoPendente.getStatus());
            assertNotNull(pagamentoPendente.getPagamento());
            assertTrue(matriculaMock.isAtiva());

            verify(pagamentoRepository, times(1)).saveAndFlush(pagamentoPendente);
            verify(matriculaRepositoy, times(1)).saveAndFlush(matriculaMock);
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se o pagamento não for encontrado")
        void deveLancarErroQuandoPagamentoNaoEncontrado() {
            when(pagamentoRepository.findByGatewayId("id-inexistente"))
                    .thenReturn(Optional.empty());

            assertThrows(
                    BadRequestException.class,
                    () -> pagamentoService.processarNotificacaoMercadoPago("id-inexistente")
            );

            verify(pagamentoRepository, never()).saveAndFlush(any());
            verify(matriculaRepositoy, never()).saveAndFlush(any());
        }
    }
}