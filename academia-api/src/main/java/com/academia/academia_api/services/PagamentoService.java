package com.academia.academia_api.services;

import com.academia.academia_api.DTOs.PixCobrancaResponse;
import com.academia.academia_api.entity.Matricula;
import com.academia.academia_api.entity.enums.MetodoPagamentoEnum;
import com.academia.academia_api.entity.Pagamento;
import com.academia.academia_api.entity.enums.StatusPagamentoEnum;
import com.academia.academia_api.infra.gateway.PixGateway;
import com.academia.academia_api.repository.PagamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PixGateway pixGateway;

    public PagamentoService(PagamentoRepository pagamentoRepository, PixGateway pixGateway) {
        this.pagamentoRepository = pagamentoRepository;
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
}