package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.PixCobrancaResponse;
import com.academia.academia_api.entity.Aluno;
import com.academia.academia_api.entity.Matricula;
import com.academia.academia_api.entity.Pagamento;
import com.academia.academia_api.entity.Plano;
import com.academia.academia_api.entity.enums.MatriculaStatusEnum;
import com.academia.academia_api.entity.enums.MetodoPagamentoEnum;
import com.academia.academia_api.entity.enums.StatusPagamentoEnum;
import com.academia.academia_api.entity.enums.TipoPlano;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.infra.gateway.PixGateway;
import com.academia.academia_api.repository.MatriculaRepositoy;
import com.academia.academia_api.repository.PagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    private static final String GATEWAY_ID = "123456789";

    @Mock private PagamentoRepository pagamentoRepository;
    @Mock private MatriculaRepositoy matriculaRepositoy;
    @Mock private PixGateway pixGateway;

    @InjectMocks private PagamentoService service;

    private Aluno aluno;
    private Plano plano;
    private Matricula matricula;
    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setEmail("aluno@teste.com");

        plano = new Plano();
        plano.setNome("Plano Mensal");
        plano.setTipo(TipoPlano.MENSAL);
        plano.setValor(new BigDecimal("99.90"));

        matricula = new Matricula();
        matricula.setMatricula(1L);
        matricula.setAluno(aluno);
        matricula.setPlano(plano);
        matricula.setDiaVencimento(10);

        pagamento = new Pagamento();
        pagamento.setId(10L);
        pagamento.setMatricula(matricula);
        pagamento.setValor(plano.getValor());
        pagamento.setStatus(StatusPagamentoEnum.PENDENTE);
        pagamento.setGatewayId(GATEWAY_ID);
    }


    @Test
    @DisplayName("criarPagamentoPix: cria pagamento PENDENTE e preenche dados do gateway")
    void criarPagamentoPix_sucesso() {
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(inv -> {
            Pagamento p = inv.getArgument(0);
            if (p.getId() == null) p.setId(10L);
            return p;
        });

        PixCobrancaResponse resp = mock(PixCobrancaResponse.class);
        when(resp.transacaoId()).thenReturn(GATEWAY_ID);
        when(resp.codigoPix()).thenReturn("00020126...");
        when(resp.qrCodeBase64()).thenReturn("QVFS");
        when(pixGateway.gerarCobrancaPix(eq(10L), eq(plano.getValor()), anyString(), eq("aluno@teste.com")))
                .thenReturn(resp);

        Pagamento resultado = service.criarPagamentoPix(matricula);

        assertEquals(StatusPagamentoEnum.PENDENTE, resultado.getStatus());
        assertEquals(MetodoPagamentoEnum.PIX, resultado.getMetodoPagamento());
        assertEquals(plano.getValor(), resultado.getValor());
        assertEquals(GATEWAY_ID, resultado.getGatewayId());
        assertEquals("00020126...", resultado.getCodigoPix());
        assertEquals("QVFS", resultado.getQrCodeBase64());
        verify(pagamentoRepository, times(2)).save(any(Pagamento.class));
    }

    @Test
    @DisplayName("criarPagamentoPix: e-mail em branco lança BadRequest e não chama o gateway")
    void criarPagamentoPix_emailEmBranco() {
        aluno.setEmail("  ");
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThrows(BadRequestException.class, () -> service.criarPagamentoPix(matricula));

        verifyNoInteractions(pixGateway);
    }


    @Test
    @DisplayName("confirmarPagamento: pagamento inexistente lança ResourceNotFound")
    void confirmar_naoEncontrado() {
        when(pagamentoRepository.findByGatewayId(GATEWAY_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.confirmarPagamento(GATEWAY_ID));

        verify(matriculaRepositoy, never()).save(any());
    }

    @Test
    @DisplayName("confirmarPagamento: idempotente, pagamento já PAGO não altera nada")
    void confirmar_idempotente() {
        pagamento.setStatus(StatusPagamentoEnum.PAGO);
        matricula.setStatus(MatriculaStatusEnum.ATIVA_PAGO);
        LocalDate venc = LocalDate.of(2026, 12, 10);
        matricula.setDataVencimento(venc);
        when(pagamentoRepository.findByGatewayId(GATEWAY_ID)).thenReturn(Optional.of(pagamento));

        service.confirmarPagamento(GATEWAY_ID);

        assertEquals(venc, matricula.getDataVencimento());
        verify(pagamentoRepository, never()).save(any());
        verify(matriculaRepositoy, never()).save(any());
    }

    @Test
    @DisplayName("confirmarPagamento: primeiro pagamento ativa a matrícula sem mexer na data")
    void confirmar_primeiroPagamento() {
        matricula.setStatus(MatriculaStatusEnum.AGUARDANDO_PRIMEIRO_PAGAMENTO);
        matricula.setDiaVencimento(null);
        when(pagamentoRepository.findByGatewayId(GATEWAY_ID)).thenReturn(Optional.of(pagamento));

        service.confirmarPagamento(GATEWAY_ID);

        assertEquals(StatusPagamentoEnum.PAGO, pagamento.getStatus());
        assertNotNull(pagamento.getPagamento());
        assertEquals(MatriculaStatusEnum.ATIVA_PAGO, matricula.getStatus());
        assertTrue(matricula.isAtiva());
        assertNull(matricula.getDataVencimento());
        verify(matriculaRepositoy).save(matricula);
    }

    @Test
    @DisplayName("confirmarPagamento: renovação em carência avança 1 mês a partir do vencimento original")
    void confirmar_renovacaoEmCarencia() {
        matricula.setStatus(MatriculaStatusEnum.ATIVA_PENDENTE_PAGAMENTO);
        matricula.setDataVencimento(LocalDate.of(2026, 12, 10));
        when(pagamentoRepository.findByGatewayId(GATEWAY_ID)).thenReturn(Optional.of(pagamento));

        service.confirmarPagamento(GATEWAY_ID);

        assertEquals(MatriculaStatusEnum.ATIVA_PAGO, matricula.getStatus());
        assertTrue(matricula.isAtiva());
        assertEquals(LocalDate.of(2027, 1, 10), matricula.getDataVencimento());
        verify(matriculaRepositoy).save(matricula);
    }

    @Test
    @DisplayName("confirmarPagamento: pagando em RESTRICAO volta a ATIVA_PAGO, limpa bloqueio e avança o ciclo do plano")
    void confirmar_pagamentoEmRestricao() {
        plano.setTipo(TipoPlano.TRIMESTRAL);
        matricula.setStatus(MatriculaStatusEnum.RESTRICAO_FALTA_PAGAMENTO);
        matricula.setDataVencimento(LocalDate.of(2026, 12, 10));
        matricula.setDataBloqueio(LocalDate.of(2026, 12, 16));
        when(pagamentoRepository.findByGatewayId(GATEWAY_ID)).thenReturn(Optional.of(pagamento));

        service.confirmarPagamento(GATEWAY_ID);

        assertEquals(MatriculaStatusEnum.ATIVA_PAGO, matricula.getStatus());
        assertTrue(matricula.isAtiva());
        assertNull(matricula.getDataBloqueio());
        assertEquals(LocalDate.of(2027, 3, 10), matricula.getDataVencimento());
    }

    @Test
    @DisplayName("confirmarPagamento: reativação reinicia o ciclo a partir de hoje no dia de vencimento escolhido")
    void confirmar_reativacao() {
        matricula.setStatus(MatriculaStatusEnum.INATIVA_PENDENTE_PAGAMENTO);
        matricula.setDataVencimento(LocalDate.of(2025, 1, 10));
        when(pagamentoRepository.findByGatewayId(GATEWAY_ID)).thenReturn(Optional.of(pagamento));

        service.confirmarPagamento(GATEWAY_ID);

        LocalDate esperado = LocalDate.now().plusMonths(1).withDayOfMonth(10);
        assertEquals(MatriculaStatusEnum.ATIVA_PAGO, matricula.getStatus());
        assertTrue(matricula.isAtiva());
        assertEquals(esperado, matricula.getDataVencimento());
        assertNull(matricula.getDataBloqueio());
    }

    @Test
    @DisplayName("confirmarPagamento: status inesperado marca o pagamento PAGO mas não altera o ciclo")
    void confirmar_statusInesperado() {
        LocalDate venc = LocalDate.of(2026, 12, 10);
        matricula.setStatus(MatriculaStatusEnum.ATIVA_PAGO);
        matricula.setDataVencimento(venc);
        when(pagamentoRepository.findByGatewayId(GATEWAY_ID)).thenReturn(Optional.of(pagamento));

        service.confirmarPagamento(GATEWAY_ID);

        assertEquals(StatusPagamentoEnum.PAGO, pagamento.getStatus());
        assertEquals(MatriculaStatusEnum.ATIVA_PAGO, matricula.getStatus());
        assertEquals(venc, matricula.getDataVencimento());
    }

    @Test
    @DisplayName("processarNotificacao: status approved confirma o pagamento")
    void notificacao_approved() {
        matricula.setStatus(MatriculaStatusEnum.AGUARDANDO_PRIMEIRO_PAGAMENTO);
        when(pixGateway.consultarStatus(GATEWAY_ID)).thenReturn("approved");
        when(pagamentoRepository.findByGatewayId(GATEWAY_ID)).thenReturn(Optional.of(pagamento));

        service.processarNotificacaoMercadoPago(GATEWAY_ID);

        assertEquals(StatusPagamentoEnum.PAGO, pagamento.getStatus());
        assertEquals(MatriculaStatusEnum.ATIVA_PAGO, matricula.getStatus());
    }

    @Test
    @DisplayName("processarNotificacao: status diferente de approved não faz nada")
    void notificacao_naoApproved() {
        when(pixGateway.consultarStatus(GATEWAY_ID)).thenReturn("pending");

        service.processarNotificacaoMercadoPago(GATEWAY_ID);

        verify(pagamentoRepository, never()).findByGatewayId(anyString());
        verifyNoInteractions(matriculaRepositoy);
    }


    @Test
    @DisplayName("cancelarPendentes: cancela no gateway e marca todos os pendentes como CANCELADO")
    void cancelarPendentes_cancelaTodos() {
        Pagamento outro = new Pagamento();
        outro.setId(11L);
        outro.setMatricula(matricula);
        outro.setStatus(StatusPagamentoEnum.PENDENTE);
        outro.setGatewayId("987654321");

        when(pagamentoRepository.findByMatriculaMatriculaAndStatus(1L, StatusPagamentoEnum.PENDENTE))
                .thenReturn(List.of(pagamento, outro));

        service.cancelarPendentes(matricula);

        assertEquals(StatusPagamentoEnum.CANCELADO, pagamento.getStatus());
        assertEquals(StatusPagamentoEnum.CANCELADO, outro.getStatus());
        verify(pixGateway).cancelarCobranca(GATEWAY_ID);
        verify(pixGateway).cancelarCobranca("987654321");
        verify(pagamentoRepository, times(2)).save(any(Pagamento.class));
    }

    @Test
    @DisplayName("cancelarPendentes: sem pendentes não chama o gateway")
    void cancelarPendentes_semPendentes() {
        when(pagamentoRepository.findByMatriculaMatriculaAndStatus(1L, StatusPagamentoEnum.PENDENTE))
                .thenReturn(List.of());

        service.cancelarPendentes(matricula);

        verifyNoInteractions(pixGateway);
        verify(pagamentoRepository, never()).save(any());
    }
}