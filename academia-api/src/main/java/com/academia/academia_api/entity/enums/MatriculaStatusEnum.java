package com.academia.academia_api.entity.enums;

public enum MatriculaStatusEnum {

    // Matrícula criada, primeiro Pix ainda não pago. Sem acesso.
    AGUARDANDO_PRIMEIRO_PAGAMENTO,

    // Em dia. Com acesso.
    ATIVA_PAGO,

    // Em dia, mas pediu cancelamento: usa até o vencimento, que não gera nova cobrança. Com acesso.
    ATIVA_CANCELAMENTO_AGENDADO,

    // Venceu e está dentro dos 5 dias de carência. Com acesso, cobrança enviada.
    ATIVA_PENDENTE_PAGAMENTO,

    // Passou dos 5 dias sem pagar. Sem acesso, mas pode pagar e voltar (até 1 mês depois do bloqueio).
    RESTRICAO_FALTA_PAGAMENTO,

    // Passou 1 mês bloqueado sem pagar. Cobrança antiga anulada para não acumular. Sem acesso.
    INATIVA,

    // Aluno inativo decidiu voltar e já tem um novo pagamento gerado. Sem acesso até pagar.
    INATIVA_PENDENTE_PAGAMENTO,

    // Cancelamento voluntário concluído (chegou o vencimento e não renovou). Sem acesso.
    CANCELADA;

    public boolean temAcesso() {
        return this == ATIVA_PAGO
                || this == ATIVA_CANCELAMENTO_AGENDADO
                || this == ATIVA_PENDENTE_PAGAMENTO;
    }
}