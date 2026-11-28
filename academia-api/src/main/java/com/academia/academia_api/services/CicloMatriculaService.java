package com.academia.academia_api.services;

import com.academia.academia_api.entity.Matricula;
import com.academia.academia_api.entity.Pagamento;
import com.academia.academia_api.entity.enums.MatriculaStatusEnum;
import com.academia.academia_api.repository.MatriculaRepositoy;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import org.slf4j.Logger;

@Service
public class CicloMatriculaService {

    public static final int DIAS_CARENCIA = 5;
    private static final Logger log = LoggerFactory.getLogger(CicloMatriculaService.class);

    private final MatriculaRepositoy matriculaRepositoy;
    private final PagamentoService pagamentoService;
    private final NotificacaoCobrancaService notificacao;

    public CicloMatriculaService(MatriculaRepositoy matriculaRepositoy,
                                 PagamentoService pagamentoService,
                                 NotificacaoCobrancaService notificacao) {
        this.matriculaRepositoy = matriculaRepositoy;
        this.pagamentoService = pagamentoService;
        this.notificacao = notificacao;
    }

    @Transactional
    public void processarVencimento(Long id, LocalDate hoje) {
        Matricula m = matriculaRepositoy.findById(id).orElseThrow();

        if (m.getDataVencimento() == null || m.getDataVencimento().isAfter(hoje)) return;

        if (m.getStatus() == MatriculaStatusEnum.ATIVA_CANCELAMENTO_AGENDADO) {
            m.setStatus(MatriculaStatusEnum.CANCELADA);
            matriculaRepositoy.save(m);
            return;
        }
        if (m.getStatus() != MatriculaStatusEnum.ATIVA_PAGO) return;

        if (m.getPlanoAgendado() != null) {
            m.setPlano(m.getPlanoAgendado());
            m.setPlanoAgendado(null);
        }

        m.setStatus(MatriculaStatusEnum.ATIVA_PENDENTE_PAGAMENTO);
        matriculaRepositoy.save(m);

        Pagamento pagamento = pagamentoService.criarPagamentoPix(m);

        try {
            notificacao.enviarCobranca(m, pagamento);
        } catch (Exception e) {
            log.error("Falha ao enviar cobrança da matrícula {}", id, e);
        }
    }

    @Transactional
    public void bloquear(Long id, LocalDate hoje) {
        Matricula m = matriculaRepositoy.findById(id).orElseThrow();

        if (m.getStatus() != MatriculaStatusEnum.ATIVA_PENDENTE_PAGAMENTO) return;
        if (!m.getDataVencimento().isBefore(hoje.minusDays(DIAS_CARENCIA))) return;

        m.setStatus(MatriculaStatusEnum.RESTRICAO_FALTA_PAGAMENTO);
        m.setDataBloqueio(hoje);
        matriculaRepositoy.save(m);
    }

    @Transactional
    public void inativar(Long id, LocalDate hoje) {
        Matricula m = matriculaRepositoy.findById(id).orElseThrow();

        if (m.getStatus() != MatriculaStatusEnum.RESTRICAO_FALTA_PAGAMENTO) return;
        if (m.getDataBloqueio() == null || hoje.isBefore(m.getDataBloqueio().plusMonths(1))) return;

        pagamentoService.cancelarPendentes(m);
        m.setStatus(MatriculaStatusEnum.INATIVA);
        matriculaRepositoy.save(m);
    }
}
