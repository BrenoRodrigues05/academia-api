package com.academia.academia_api.Jobs;

import com.academia.academia_api.entity.enums.MatriculaStatusEnum;
import com.academia.academia_api.repository.MatriculaRepositoy;
import com.academia.academia_api.services.CicloMatriculaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Consumer;

@EnableScheduling
public class CobrancaRecorrenteJob {

    private static final Logger log = LoggerFactory.getLogger(CobrancaRecorrenteJob.class);
    private static final ZoneId ZONA = ZoneId.of("America/Recife");

    private final MatriculaRepositoy matriculaRepositoy;
    private final CicloMatriculaService ciclo;

    public CobrancaRecorrenteJob(MatriculaRepositoy matriculaRepositoy, CicloMatriculaService ciclo) {
        this.matriculaRepositoy = matriculaRepositoy;
        this.ciclo = ciclo;
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "America/Recife")
    public void executar() {
        LocalDate hoje = LocalDate.now(ZONA);

        processar("inativação",
                matriculaRepositoy.findIdsBloqueadosAte(
                        MatriculaStatusEnum.RESTRICAO_FALTA_PAGAMENTO, hoje.minusMonths(1)),
                id -> ciclo.inativar(id, hoje));

        processar("bloqueio",
                matriculaRepositoy.findIdsComVencimentoAntesDe(
                        MatriculaStatusEnum.ATIVA_PENDENTE_PAGAMENTO, hoje.minusDays(CicloMatriculaService.DIAS_CARENCIA)),
                id -> ciclo.bloquear(id, hoje));

        processar("vencimento",
                matriculaRepositoy.findIdsVencidos(
                        List.of(MatriculaStatusEnum.ATIVA_PAGO, MatriculaStatusEnum.ATIVA_CANCELAMENTO_AGENDADO), hoje),
                id -> ciclo.processarVencimento(id, hoje));
    }

    private void processar(String etapa, List<Long> ids, Consumer<Long> acao) {
        log.info("[Cobrança] {}: {} matrícula(s)", etapa, ids.size());
        for (Long id : ids) {
            try {
                acao.accept(id);
            } catch (Exception e) {
                log.error("[Cobrança] Falha em {} da matrícula {}", etapa, id, e);
            }
        }
    }
}
