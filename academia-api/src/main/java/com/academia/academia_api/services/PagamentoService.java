package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.PixCobrancaResponse;
import com.academia.academia_api.entity.Matricula;
import com.academia.academia_api.entity.enums.MatriculaStatusEnum;
import com.academia.academia_api.entity.enums.MetodoPagamentoEnum;
import com.academia.academia_api.entity.Pagamento;
import com.academia.academia_api.entity.enums.StatusPagamentoEnum;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.infra.gateway.PixGateway;
import com.academia.academia_api.repository.MatriculaRepositoy;
import com.academia.academia_api.repository.PagamentoRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private  final MatriculaRepositoy matriculaRepositoy;
    private final PixGateway pixGateway;

    public PagamentoService(PagamentoRepository pagamentoRepository, MatriculaRepositoy matriculaRepositoy, PixGateway pixGateway) {
        this.pagamentoRepository = pagamentoRepository;
        this.matriculaRepositoy = matriculaRepositoy;
        this.pixGateway = pixGateway;
    }

    @Transactional
    public Pagamento criarPagamentoPix(Matricula matricula) {
        Pagamento pagamento = new Pagamento();
        pagamento.setMatricula(matricula);
        pagamento.setValor(matricula.getPlano().getValor());
        pagamento.setMetodoPagamento(MetodoPagamentoEnum.PIX);
        pagamento.setStatus(StatusPagamentoEnum.PENDENTE);
        pagamento.setDataCriacao(LocalDateTime.now());

        pagamento = pagamentoRepository.save(pagamento);

        String descricao = "Matrícula Plano: " + matricula.getPlano().getNome();

        String emailAluno = matricula.getAluno().getEmail();
        if (emailAluno == null || emailAluno.isBlank()) {
            throw new BadRequestException("E-mail do aluno é obrigatório para gerar o Pix.");
        }

        PixCobrancaResponse responsePix = pixGateway.gerarCobrancaPix(
                pagamento.getId(),
                pagamento.getValor(),
                descricao,
                emailAluno.trim()
        );

        pagamento.setGatewayId(responsePix.transacaoId());
        pagamento.setCodigoPix(responsePix.codigoPix());
        pagamento.setQrCodeBase64(responsePix.qrCodeBase64());
        pagamento.setExpiracao(responsePix.dataexpiracao());

        return pagamentoRepository.save(pagamento);
    }

    @Transactional
    public void processarNotificacaoMercadoPago(String idTransacaoGateway) {
        String status = pixGateway.consultarStatus(idTransacaoGateway);

        if (!"approved".equals(status)) {
            System.out.println(">>> Pagamento " + idTransacaoGateway + " com status '" + status + "', nada a fazer.");
            return;
        }
        confirmarPagamento(idTransacaoGateway);
    }

    @Transactional
    public void confirmarPagamento(String idTransacaoGateway) {
        Pagamento pagamento = pagamentoRepository.findByGatewayId(idTransacaoGateway)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pagamento não encontrado para o ID de transação: " + idTransacaoGateway));

        if (pagamento.getStatus() != StatusPagamentoEnum.PENDENTE) return;

        pagamento.setStatus(StatusPagamentoEnum.PAGO);
        pagamento.setPagamento(LocalDateTime.now());
        pagamentoRepository.save(pagamento);

        Matricula m = pagamento.getMatricula();
        int meses = m.getPlano().getTipo().meses();

        switch (m.getStatus()) {
            case AGUARDANDO_PRIMEIRO_PAGAMENTO ->
                    m.setStatus(MatriculaStatusEnum.ATIVA_PAGO);

            case ATIVA_PENDENTE_PAGAMENTO, RESTRICAO_FALTA_PAGAMENTO -> {
                m.setDataVencimento(m.getDataVencimento().plusMonths(meses));
                m.setDataBloqueio(null);
                m.setStatus(MatriculaStatusEnum.ATIVA_PAGO);
            }

            case INATIVA_PENDENTE_PAGAMENTO -> {
                LocalDate base = LocalDate.now().plusMonths(meses);
                m.setDataVencimento(base.withDayOfMonth(m.getDiaVencimento()));
                m.setDataBloqueio(null);
                m.setStatus(MatriculaStatusEnum.ATIVA_PAGO);
            }

            default -> System.out.println(">>> Pagamento " + idTransacaoGateway
                    + " recebido com matrícula em " + m.getStatus() + ", revisar manualmente.");
        }
        matriculaRepositoy.save(m);
    }

    @Transactional
    public void cancelarPendentes(Matricula matricula) {
        pagamentoRepository.findByMatriculaMatriculaAndStatus(
                matricula.getMatricula(), StatusPagamentoEnum.PENDENTE
        ).forEach(p -> {
            pixGateway.cancelarCobranca(p.getGatewayId());
            p.setStatus(StatusPagamentoEnum.CANCELADO);
            pagamentoRepository.save(p);
        });
    }
}