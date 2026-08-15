package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.PixCobrancaResponse;
import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.Matricula;
import com.academia.academia_api.entity.Pagamento;
import com.academia.academia_api.entity.Plano;
import com.academia.academia_api.entity.Usuarios;
import com.academia.academia_api.entity.enums.MetodoPagamentoEnum;
import com.academia.academia_api.entity.enums.StatusPagamentoEnum;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
                    eq("aluno.teste")
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
                    eq("aluno.teste")
            );
        }
    }
}