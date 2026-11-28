package com.academia.academia_api.services;

import com.academia.academia_api.entity.Matricula;
import com.academia.academia_api.entity.Pagamento;
import com.academia.academia_api.entity.Plano;
import com.academia.academia_api.entity.enums.MatriculaStatusEnum;
import com.academia.academia_api.repository.MatriculaRepositoy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CicloMatriculaServiceTest {

    private static final Long ID = 1L;
    private static final LocalDate HOJE = LocalDate.of(2026, 12, 16);

    @Mock private MatriculaRepositoy matriculaRepositoy;
    @Mock private PagamentoService pagamentoService;
    @Mock private NotificacaoCobrancaService notificacao;

    @InjectMocks private CicloMatriculaService service;

    private Plano plano;

    @BeforeEach
    void setUp() {
        plano = new Plano();
        plano.setNome("Plano Mensal");
    }

    private Matricula matricula(MatriculaStatusEnum status, LocalDate vencimento) {
        Matricula m = new Matricula();
        m.setMatricula(ID);
        m.setPlano(plano);
        m.setStatus(status);
        m.setDataVencimento(vencimento);
        when(matriculaRepositoy.findById(ID)).thenReturn(Optional.of(m));
        return m;
    }


    @Test
    @DisplayName("vencimento: ATIVA_PAGO vencida vira PENDENTE, gera Pix e envia e-mail (com acesso mantido)")
    void vencimento_cobra() {
        Matricula m = matricula(MatriculaStatusEnum.ATIVA_PAGO, HOJE.minusDays(1));
        Pagamento pagamento = new Pagamento();
        when(pagamentoService.criarPagamentoPix(m)).thenReturn(pagamento);

        service.processarVencimento(ID, HOJE);

        assertEquals(MatriculaStatusEnum.ATIVA_PENDENTE_PAGAMENTO, m.getStatus());
        assertTrue(m.isAtiva());
        verify(matriculaRepositoy).save(m);
        verify(pagamentoService).criarPagamentoPix(m);
        verify(notificacao).enviarCobranca(m, pagamento);
    }

    @Test
    @DisplayName("vencimento: vencendo exatamente hoje já cobra")
    void vencimento_venceHoje() {
        Matricula m = matricula(MatriculaStatusEnum.ATIVA_PAGO, HOJE);
        when(pagamentoService.criarPagamentoPix(m)).thenReturn(new Pagamento());

        service.processarVencimento(ID, HOJE);

        assertEquals(MatriculaStatusEnum.ATIVA_PENDENTE_PAGAMENTO, m.getStatus());
    }

    @Test
    @DisplayName("vencimento: vencimento futuro não faz nada")
    void vencimento_futuro() {
        Matricula m = matricula(MatriculaStatusEnum.ATIVA_PAGO, HOJE.plusDays(1));

        service.processarVencimento(ID, HOJE);

        assertEquals(MatriculaStatusEnum.ATIVA_PAGO, m.getStatus());
        verifyNoInteractions(pagamentoService, notificacao);
        verify(matriculaRepositoy, never()).save(any());
    }

    @Test
    @DisplayName("vencimento: dataVencimento nula (dia ainda não escolhido) é ignorada")
    void vencimento_semData() {
        Matricula m = matricula(MatriculaStatusEnum.ATIVA_PAGO, null);

        service.processarVencimento(ID, HOJE);

        assertEquals(MatriculaStatusEnum.ATIVA_PAGO, m.getStatus());
        verifyNoInteractions(pagamentoService, notificacao);
    }

    @Test
    @DisplayName("vencimento: troca de plano agendada é aplicada antes de gerar a cobrança")
    void vencimento_aplicaPlanoAgendado() {
        Plano novo = new Plano();
        novo.setNome("Plano Anual");
        Matricula m = matricula(MatriculaStatusEnum.ATIVA_PAGO, HOJE);
        m.setPlanoAgendado(novo);
        when(pagamentoService.criarPagamentoPix(m)).thenReturn(new Pagamento());

        service.processarVencimento(ID, HOJE);

        assertSame(novo, m.getPlano());
        assertNull(m.getPlanoAgendado());
    }

    @Test
    @DisplayName("vencimento: cancelamento agendado vencido vira CANCELADA, sem acesso e sem cobrança")
    void vencimento_cancelamentoAgendado() {
        Matricula m = matricula(MatriculaStatusEnum.ATIVA_CANCELAMENTO_AGENDADO, HOJE);

        service.processarVencimento(ID, HOJE);

        assertEquals(MatriculaStatusEnum.CANCELADA, m.getStatus());
        assertFalse(m.isAtiva());
        verify(matriculaRepositoy).save(m);
        verifyNoInteractions(pagamentoService, notificacao);
    }

    @Test
    @DisplayName("vencimento: cancelamento agendado ainda no prazo mantém acesso")
    void vencimento_cancelamentoAgendadoNoPrazo() {
        Matricula m = matricula(MatriculaStatusEnum.ATIVA_CANCELAMENTO_AGENDADO, HOJE.plusDays(3));

        service.processarVencimento(ID, HOJE);

        assertEquals(MatriculaStatusEnum.ATIVA_CANCELAMENTO_AGENDADO, m.getStatus());
        assertTrue(m.isAtiva());
    }

    @Test
    @DisplayName("vencimento: status diferente de ATIVA_PAGO/CANCELAMENTO_AGENDADO é ignorado (idempotência)")
    void vencimento_statusIgnorado() {
        Matricula m = matricula(MatriculaStatusEnum.ATIVA_PENDENTE_PAGAMENTO, HOJE.minusDays(2));

        service.processarVencimento(ID, HOJE);

        assertEquals(MatriculaStatusEnum.ATIVA_PENDENTE_PAGAMENTO, m.getStatus());
        verifyNoInteractions(pagamentoService, notificacao);
    }

    @Test
    @DisplayName("vencimento: falha no e-mail não derruba o processamento")
    void vencimento_falhaEmail() {
        Matricula m = matricula(MatriculaStatusEnum.ATIVA_PAGO, HOJE);
        Pagamento pagamento = new Pagamento();
        when(pagamentoService.criarPagamentoPix(m)).thenReturn(pagamento);
        doThrow(new RuntimeException("smtp fora")).when(notificacao).enviarCobranca(m, pagamento);

        assertDoesNotThrow(() -> service.processarVencimento(ID, HOJE));

        assertEquals(MatriculaStatusEnum.ATIVA_PENDENTE_PAGAMENTO, m.getStatus());
    }

    @Test
    @DisplayName("vencimento: falha no gateway propaga (o @Transactional faz rollback) e não envia e-mail")
    void vencimento_falhaGateway() {
        Matricula m = matricula(MatriculaStatusEnum.ATIVA_PAGO, HOJE);
        when(pagamentoService.criarPagamentoPix(m)).thenThrow(new IllegalStateException("gateway fora"));

        assertThrows(IllegalStateException.class, () -> service.processarVencimento(ID, HOJE));

        verifyNoInteractions(notificacao);
    }


    @Test
    @DisplayName("bloquear: 6 dias após o vencimento bloqueia, registra a data e tira o acesso")
    void bloquear_passouCarencia() {
        Matricula m = matricula(MatriculaStatusEnum.ATIVA_PENDENTE_PAGAMENTO, HOJE.minusDays(6));

        service.bloquear(ID, HOJE);

        assertEquals(MatriculaStatusEnum.RESTRICAO_FALTA_PAGAMENTO, m.getStatus());
        assertEquals(HOJE, m.getDataBloqueio());
        assertFalse(m.isAtiva());
        verify(matriculaRepositoy).save(m);
    }

    @Test
    @DisplayName("bloquear: no 5º dia ainda está na carência, não bloqueia")
    void bloquear_ultimoDiaDeCarencia() {
        Matricula m = matricula(MatriculaStatusEnum.ATIVA_PENDENTE_PAGAMENTO, HOJE.minusDays(5));

        service.bloquear(ID, HOJE);

        assertEquals(MatriculaStatusEnum.ATIVA_PENDENTE_PAGAMENTO, m.getStatus());
        assertTrue(m.isAtiva());
        assertNull(m.getDataBloqueio());
        verify(matriculaRepositoy, never()).save(any());
    }

    @Test
    @DisplayName("bloquear: status diferente de ATIVA_PENDENTE_PAGAMENTO é ignorado")
    void bloquear_statusIgnorado() {
        Matricula m = matricula(MatriculaStatusEnum.ATIVA_PAGO, HOJE.minusDays(30));

        service.bloquear(ID, HOJE);

        assertEquals(MatriculaStatusEnum.ATIVA_PAGO, m.getStatus());
        verify(matriculaRepositoy, never()).save(any());
    }


    @Test
    @DisplayName("inativar: 1 mês após o bloqueio vira INATIVA e anula as cobranças pendentes")
    void inativar_umMesDepois() {
        Matricula m = matricula(MatriculaStatusEnum.RESTRICAO_FALTA_PAGAMENTO, HOJE.minusDays(40));
        m.setDataBloqueio(HOJE.minusMonths(1));

        service.inativar(ID, HOJE);

        assertEquals(MatriculaStatusEnum.INATIVA, m.getStatus());
        assertFalse(m.isAtiva());
        verify(pagamentoService).cancelarPendentes(m);
        verify(matriculaRepositoy).save(m);
    }

    @Test
    @DisplayName("inativar: um dia antes de completar 1 mês continua em RESTRICAO")
    void inativar_antesDoPrazo() {
        Matricula m = matricula(MatriculaStatusEnum.RESTRICAO_FALTA_PAGAMENTO, HOJE.minusDays(40));
        m.setDataBloqueio(HOJE.minusMonths(1).plusDays(1));

        service.inativar(ID, HOJE);

        assertEquals(MatriculaStatusEnum.RESTRICAO_FALTA_PAGAMENTO, m.getStatus());
        verifyNoInteractions(pagamentoService);
        verify(matriculaRepositoy, never()).save(any());
    }

    @Test
    @DisplayName("inativar: sem dataBloqueio não faz nada")
    void inativar_semDataBloqueio() {
        Matricula m = matricula(MatriculaStatusEnum.RESTRICAO_FALTA_PAGAMENTO, HOJE.minusDays(40));

        service.inativar(ID, HOJE);

        assertEquals(MatriculaStatusEnum.RESTRICAO_FALTA_PAGAMENTO, m.getStatus());
        verifyNoInteractions(pagamentoService);
    }

    @Test
    @DisplayName("inativar: status diferente de RESTRICAO é ignorado")
    void inativar_statusIgnorado() {
        Matricula m = matricula(MatriculaStatusEnum.ATIVA_PAGO, HOJE.minusDays(40));
        m.setDataBloqueio(HOJE.minusMonths(2));

        service.inativar(ID, HOJE);

        assertEquals(MatriculaStatusEnum.ATIVA_PAGO, m.getStatus());
        verifyNoInteractions(pagamentoService);
    }
}