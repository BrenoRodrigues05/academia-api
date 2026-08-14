package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.PixCobrancaResponse;
import com.academia.academia_api.entity.Matricula;
import com.academia.academia_api.entity.enums.MetodoPagamentoEnum;
import com.academia.academia_api.entity.Pagamento;
import com.academia.academia_api.entity.enums.StatusPagamentoEnum;
import com.academia.academia_api.infra.exceptions.BadRequestException;
import com.academia.academia_api.infra.exceptions.ResourceNotFoundException;
import com.academia.academia_api.infra.gateway.PixGateway;
import com.academia.academia_api.repository.MatriculaRepositoy;
import com.academia.academia_api.repository.PagamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        String usernameAluno = matricula.getAluno().getUsuario().getUsername();

        PixCobrancaResponse responsePix = pixGateway.gerarCobrancaPix(
                pagamento.getId(),
                pagamento.getValor(),
                descricao,
                usernameAluno
        );

        pagamento.setGatewayId(responsePix.transacaoId());
        pagamento.setCodigoPix(responsePix.codigoPix());
        pagamento.setQrCodeBase64(responsePix.qrCodeBase64());
        pagamento.setExpiracao(responsePix.dataexpiracao());

        return pagamentoRepository.save(pagamento);
    }

    @Transactional
    public void processarNotificacaoMercadoPago(String idTransacaoGateway) {
        try {
            com.mercadopago.client.payment.PaymentClient client = new com.mercadopago.client.payment.PaymentClient();
            com.mercadopago.resources.payment.Payment payment = client.get(Long.parseLong(idTransacaoGateway));

            Pagamento pagamento = pagamentoRepository.findByGatewayId(idTransacaoGateway)
                    .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado para o ID de transação: " + idTransacaoGateway));

            if ("approved".equalsIgnoreCase(payment.getStatus()) && pagamento.getStatus() == StatusPagamentoEnum.PENDENTE) {

                pagamento.setStatus(StatusPagamentoEnum.PAGO);
                pagamento.setPagamento(LocalDateTime.now());
                pagamentoRepository.save(pagamento);

                Matricula matricula = pagamento.getMatricula();
                matricula.setAtiva(true);
                matriculaRepositoy.save(matricula);
            }

        } catch (Exception e) {
            throw new BadRequestException("Erro ao processar notificação do Mercado Pago: " + e.getMessage());
        }
    }
}