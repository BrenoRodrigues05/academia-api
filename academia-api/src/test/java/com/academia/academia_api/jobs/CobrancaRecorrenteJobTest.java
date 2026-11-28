package com.academia.academia_api.jobs;

import com.academia.academia_api.Jobs.CobrancaRecorrenteJob;
import com.academia.academia_api.entity.enums.MatriculaStatusEnum;
import com.academia.academia_api.repository.MatriculaRepositoy;
import com.academia.academia_api.services.CicloMatriculaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CobrancaRecorrenteJobTest {

    private static final ZoneId ZONA = ZoneId.of("America/Recife");

    @Mock private MatriculaRepositoy matriculaRepositoy;
    @Mock private CicloMatriculaService ciclo;

    @InjectMocks private CobrancaRecorrenteJob job;

    @Test
    @DisplayName("executar: processa cada matrícula retornada em cada etapa")
    void processaTodasAsEtapas() {
        when(matriculaRepositoy.findIdsBloqueadosAte(
                eq(MatriculaStatusEnum.RESTRICAO_FALTA_PAGAMENTO), any(LocalDate.class)))
                .thenReturn(List.of(1L, 2L));
        when(matriculaRepositoy.findIdsComVencimentoAntesDe(
                eq(MatriculaStatusEnum.ATIVA_PENDENTE_PAGAMENTO), any(LocalDate.class)))
                .thenReturn(List.of(3L));
        when(matriculaRepositoy.findIdsVencidos(anyCollection(), any(LocalDate.class)))
                .thenReturn(List.of(4L, 5L));

        job.executar();

        verify(ciclo).inativar(eq(1L), any(LocalDate.class));
        verify(ciclo).inativar(eq(2L), any(LocalDate.class));
        verify(ciclo).bloquear(eq(3L), any(LocalDate.class));
        verify(ciclo).processarVencimento(eq(4L), any(LocalDate.class));
        verify(ciclo).processarVencimento(eq(5L), any(LocalDate.class));
        verifyNoMoreInteractions(ciclo);
    }

    @Test
    @DisplayName("executar: etapas rodam da mais avançada para a mais inicial (1 etapa por matrícula por execução)")
    void respeitaOrdemDasEtapas() {
        when(matriculaRepositoy.findIdsBloqueadosAte(any(), any(LocalDate.class))).thenReturn(List.of(1L));
        when(matriculaRepositoy.findIdsComVencimentoAntesDe(any(), any(LocalDate.class))).thenReturn(List.of(2L));
        when(matriculaRepositoy.findIdsVencidos(anyCollection(), any(LocalDate.class))).thenReturn(List.of(3L));

        job.executar();

        InOrder ordem = inOrder(ciclo);
        ordem.verify(ciclo).inativar(eq(1L), any(LocalDate.class));
        ordem.verify(ciclo).bloquear(eq(2L), any(LocalDate.class));
        ordem.verify(ciclo).processarVencimento(eq(3L), any(LocalDate.class));
    }

    @Test
    @DisplayName("executar: falha em uma matrícula não impede as demais nem as próximas etapas")
    void falhaNaoDerrubaAsOutras() {
        when(matriculaRepositoy.findIdsBloqueadosAte(any(), any(LocalDate.class))).thenReturn(List.of(1L));
        when(matriculaRepositoy.findIdsComVencimentoAntesDe(any(), any(LocalDate.class))).thenReturn(List.of(2L));
        when(matriculaRepositoy.findIdsVencidos(anyCollection(), any(LocalDate.class)))
                .thenReturn(List.of(3L, 4L));

        doThrow(new IllegalStateException("gateway fora"))
                .when(ciclo).inativar(eq(1L), any(LocalDate.class));
        doThrow(new IllegalStateException("gateway fora"))
                .when(ciclo).processarVencimento(eq(3L), any(LocalDate.class));

        assertDoesNotThrow(() -> job.executar());

        verify(ciclo).bloquear(eq(2L), any(LocalDate.class));
        verify(ciclo).processarVencimento(eq(4L), any(LocalDate.class));
    }

    @Test
    @DisplayName("executar: sem matrículas pendentes não aciona o serviço de ciclo")
    void semMatriculas() {
        job.executar();

        verifyNoInteractions(ciclo);
    }

    @Test
    @DisplayName("executar: passa às queries as datas de corte corretas (1 mês, 5 dias de carência, hoje)")
    void datasDeCorte() {
        LocalDate hoje = LocalDate.now(ZONA);

        job.executar();

        verify(matriculaRepositoy).findIdsBloqueadosAte(
                MatriculaStatusEnum.RESTRICAO_FALTA_PAGAMENTO, hoje.minusMonths(1));
        verify(matriculaRepositoy).findIdsComVencimentoAntesDe(
                MatriculaStatusEnum.ATIVA_PENDENTE_PAGAMENTO, hoje.minusDays(5));
        verify(matriculaRepositoy).findIdsVencidos(
                argThat(status -> status.size() == 2
                        && status.contains(MatriculaStatusEnum.ATIVA_PAGO)
                        && status.contains(MatriculaStatusEnum.ATIVA_CANCELAMENTO_AGENDADO)),
                eq(hoje));
    }

    @Test
    @DisplayName("executar: a mesma data de referência é repassada ao serviço de ciclo")
    void repassaDataDeReferencia() {
        LocalDate hoje = LocalDate.now(ZONA);
        when(matriculaRepositoy.findIdsVencidos(anyCollection(), any(LocalDate.class))).thenReturn(List.of(9L));

        job.executar();

        verify(ciclo).processarVencimento(9L, hoje);
    }
}