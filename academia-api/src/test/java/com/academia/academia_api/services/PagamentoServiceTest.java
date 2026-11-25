
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
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Pagamentos")
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
        @DisplayName("Deve criar pagamento PIX com sucesso")
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

                        Pagamento pagamento = invocation.getArgument(0);

                        if (pagamento.getId() == null) {
                            pagamento.setId(100L);
                        }

                        return pagamento;
                    });

            when(pixGateway.gerarCobrancaPix(
                    eq(100L),
                    eq(new BigDecimal("100.00")),
                    eq("Matrícula Plano: Plano Mensal"),
                    eq("aluno@teste.com")
            )).thenReturn(pixResponse);

            Pagamento resultado =
                    pagamentoService.criarPagamentoPix(matriculaMock);

            assertNotNull(resultado);
            assertEquals(100L, resultado.getId());
            assertEquals(new BigDecimal("100.00"), resultado.getValor());

            assertEquals(
                    MetodoPagamentoEnum.PIX,
                    resultado.getMetodoPagamento()
            );

            assertEquals(
                    StatusPagamentoEnum.PENDENTE,
                    resultado.getStatus()
            );

            assertEquals(
                    "transacao-gw-123",
                    resultado.getGatewayId()
            );

            assertEquals(
                    "000201...copiaECola",
                    resultado.getCodigoPix()
            );

            assertEquals(
                    "data:image/png;base64,iVBORw0KGgo...",
                    resultado.getQrCodeBase64()
            );

            assertNotNull(resultado.getDataCriacao());
            assertNotNull(resultado.getExpiracao());

            verify(pagamentoRepository, times(2))
                    .save(any(Pagamento.class));

            verify(pixGateway, times(1))
                    .gerarCobrancaPix(
                            eq(100L),
                            eq(new BigDecimal("100.00")),
                            eq("Matrícula Plano: Plano Mensal"),
                            eq("aluno@teste.com")
                    );
        }

        @Test
        @DisplayName("Deve lançar exceção quando o email for nulo")
        void deveLancarExcecaoQuandoEmailForNulo() {

            alunoMock.setEmail(null);

            when(pagamentoRepository.save(any(Pagamento.class)))
                    .thenAnswer(invocation -> {

                        Pagamento pagamento = invocation.getArgument(0);
                        pagamento.setId(100L);

                        return pagamento;
                    });

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> pagamentoService.criarPagamentoPix(matriculaMock)
            );

            assertEquals(
                    "E-mail do aluno é obrigatório para gerar o Pix.",
                    exception.getMessage()
            );

            verify(pixGateway, never())
                    .gerarCobrancaPix(any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando o email estiver em branco")
        void deveLancarExcecaoQuandoEmailEstiverEmBranco() {

            alunoMock.setEmail("   ");

            when(pagamentoRepository.save(any(Pagamento.class)))
                    .thenAnswer(invocation -> {

                        Pagamento pagamento = invocation.getArgument(0);
                        pagamento.setId(100L);

                        return pagamento;
                    });

            assertThrows(
                    BadRequestException.class,
                    () -> pagamentoService.criarPagamentoPix(matriculaMock)
            );

            verify(pixGateway, never())
                    .gerarCobrancaPix(any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve remover espaços do email antes de gerar cobrança")
        void deveRemoverEspacosDoEmail() {

            alunoMock.setEmail("  aluno@teste.com  ");

            PixCobrancaResponse pixResponse = new PixCobrancaResponse(
                    "transacao-gw-123",
                    "codigo-pix",
                    "qr-code",
                    new BigDecimal("100.00"),
                    LocalDateTime.now().plusDays(1)
            );

            when(pagamentoRepository.save(any(Pagamento.class)))
                    .thenAnswer(invocation -> {

                        Pagamento pagamento = invocation.getArgument(0);
                        pagamento.setId(100L);

                        return pagamento;
                    });

            when(pixGateway.gerarCobrancaPix(
                    anyLong(),
                    any(BigDecimal.class),
                    anyString(),
                    eq("aluno@teste.com")
            )).thenReturn(pixResponse);

            pagamentoService.criarPagamentoPix(matriculaMock);

            verify(pixGateway).gerarCobrancaPix(
                    eq(100L),
                    any(BigDecimal.class),
                    anyString(),
                    eq("aluno@teste.com")
            );
        }
    }

    @Nested
    @DisplayName("Testes do método processarNotificacaoMercadoPago")
    class ProcessarNotificacaoMercadoPagoTests {

        @Test
        @DisplayName("Deve confirmar pagamento quando status for approved")
        void deveProcessarNotificacaoComSucesso() {

            Pagamento pagamento = criarPagamentoPendente();

            when(pixGateway.consultarStatus("transacao-gw-123"))
                    .thenReturn("approved");

            when(pagamentoRepository.findByGatewayId("transacao-gw-123"))
                    .thenReturn(Optional.of(pagamento));

            pagamentoService.processarNotificacaoMercadoPago(
                    "transacao-gw-123"
            );

            assertEquals(
                    StatusPagamentoEnum.PAGO,
                    pagamento.getStatus()
            );

            assertNotNull(pagamento.getPagamento());

            assertTrue(matriculaMock.isAtiva());

            verify(pixGateway).consultarStatus("transacao-gw-123");

            verify(pagamentoRepository).findByGatewayId(
                    "transacao-gw-123"
            );

            verify(pagamentoRepository).save(pagamento);

            verify(matriculaRepositoy).save(matriculaMock);
        }

        @Test
        @DisplayName("Não deve confirmar pagamento quando status não for approved")
        void naoDeveConfirmarQuandoStatusNaoForApproved() {

            when(pixGateway.consultarStatus("transacao-gw-123"))
                    .thenReturn("pending");

            pagamentoService.processarNotificacaoMercadoPago(
                    "transacao-gw-123"
            );

            verify(pixGateway).consultarStatus("transacao-gw-123");

            verify(pagamentoRepository, never())
                    .findByGatewayId(anyString());

            verify(pagamentoRepository, never())
                    .save(any(Pagamento.class));

            verify(matriculaRepositoy, never())
                    .save(any(Matricula.class));

            assertFalse(matriculaMock.isAtiva());
        }
    }
    @Nested
    @DisplayName("Testes do método confirmarPagamento")
    class ConfirmarPagamentoTests {

        @Test
        @DisplayName("Deve confirmar pagamento pendente e ativar matrícula")
        void deveConfirmarPagamentoComSucesso() {

            Pagamento pagamento = criarPagamentoPendente();

            when(pagamentoRepository.findByGatewayId("transacao-gw-123"))
                    .thenReturn(Optional.of(pagamento));

            pagamentoService.confirmarPagamento("transacao-gw-123");

            assertEquals(
                    StatusPagamentoEnum.PAGO,
                    pagamento.getStatus()
            );

            assertNotNull(pagamento.getPagamento());

            assertTrue(matriculaMock.isAtiva());

            verify(pagamentoRepository).save(pagamento);

            verify(matriculaRepositoy).save(matriculaMock);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando pagamento não existir")
        void deveLancarExcecaoQuandoPagamentoNaoExistir() {

            when(pagamentoRepository.findByGatewayId("id-inexistente"))
                    .thenReturn(Optional.empty());

            assertThrows(
                    ResourceNotFoundException.class,
                    () -> pagamentoService.confirmarPagamento(
                            "id-inexistente"
                    )
            );

            verify(pagamentoRepository, never())
                    .save(any(Pagamento.class));

            verify(matriculaRepositoy, never())
                    .save(any(Matricula.class));
        }

        @Test
        @DisplayName("Não deve alterar pagamento que já esteja pago")
        void naoDeveAlterarPagamentoJaPago() {

            Pagamento pagamento = criarPagamentoPendente();

            pagamento.setStatus(StatusPagamentoEnum.PAGO);

            LocalDateTime dataPagamento = LocalDateTime.now().minusDays(1);

            pagamento.setPagamento(dataPagamento);

            when(pagamentoRepository.findByGatewayId("transacao-gw-123"))
                    .thenReturn(Optional.of(pagamento));

            pagamentoService.confirmarPagamento("transacao-gw-123");

            assertEquals(
                    StatusPagamentoEnum.PAGO,
                    pagamento.getStatus()
            );

            assertEquals(dataPagamento, pagamento.getPagamento());

            assertFalse(matriculaMock.isAtiva());

            verify(pagamentoRepository, never())
                    .save(any(Pagamento.class));

            verify(matriculaRepositoy, never())
                    .save(any(Matricula.class));
        }
    }

    private Pagamento criarPagamentoPendente() {

        Pagamento pagamento = new Pagamento();

        pagamento.setId(100L);
        pagamento.setGatewayId("transacao-gw-123");
        pagamento.setValor(new BigDecimal("100.00"));
        pagamento.setMetodoPagamento(MetodoPagamentoEnum.PIX);
        pagamento.setStatus(StatusPagamentoEnum.PENDENTE);
        pagamento.setMatricula(matriculaMock);

        return pagamento;
    }
}
