# 📌 Regras de Negócio

## Usuários

* Login único
* Senha criptografada com BCrypt
* Controle por Roles
* Usuários podem ser ativados ou desativados

---

## Matrículas

### Criação

* Não é permitido criar matrícula para aluno ou plano inexistente
* Um aluno só pode ter **uma matrícula em andamento** (qualquer status, exceto `CANCELADA`)
* A matrícula nasce em `AGUARDANDO_PRIMEIRO_PAGAMENTO`, sem acesso, com um Pix gerado
* O aluno **deve** escolher o dia de vencimento (1, 5, 10, 15, 20 ou 25) após o primeiro pagamento aprovado

### Cobrança recorrente

* O job diário gera um novo Pix no vencimento e envia por e-mail ao aluno
* Após o vencimento há **5 dias de carência**, com acesso mantido (`ATIVA_PENDENTE_PAGAMENTO`)
* Passada a carência, o acesso é bloqueado (`RESTRICAO_FALTA_PAGAMENTO`), mas o aluno ainda pode pagar e voltar
* **1 mês após o bloqueio** a matrícula vira `INATIVA` e a cobrança antiga é anulada (inclusive no gateway), evitando acúmulo
* Pagar em atraso **não** desloca o ciclo: o próximo vencimento é sempre a data original + duração do plano (1, 3 ou 12 meses)

### Cancelamento e troca de plano

* O cancelamento só é permitido com a mensalidade em dia e mantém o acesso até o vencimento, sem nova cobrança (depois vira `CANCELADA`)
* A troca de plano só é permitida com a mensalidade em dia e passa a valer **no próximo vencimento**
* Durante o atraso o aluno não pode cancelar nem trocar de plano

### Reativação

* Aluno `INATIVA` pode voltar a qualquer momento: é gerado um novo Pix (`INATIVA_PENDENTE_PAGAMENTO`) e, ao pagar, o ciclo reinicia a partir da data do pagamento
* `CANCELADA` é exclusivo de cancelamento voluntário

### Acesso

* Têm acesso: `ATIVA_PAGO`, `ATIVA_CANCELAMENTO_AGENDADO` e `ATIVA_PENDENTE_PAGAMENTO`
* O campo `ativa` é derivado do `status` e nunca definido manualmente

---

## Pagamentos

* Cada confirmação consulta o status direto na gateway e é idempotente: um pagamento já confirmado não é processado de novo
* O efeito do pagamento depende do status da matrícula no momento da confirmação (primeiro pagamento, renovação, saída de restrição ou reativação)
* Falha no envio do e-mail não desfaz a cobrança; falha na gateway reverte a transição e o job tenta novamente no dia seguinte

---

## Personais

* E-mail único
* CREF único
* Relacionamento obrigatório com usuário

---

## Treinos

* Um aluno pode possuir apenas um treino ativo
* Um aluno pode visualizar seu histórico completo de treinos
* Todo treino deve possuir um personal responsável
* O personal é obtido automaticamente pelo usuário autenticado
* Apenas o personal criador pode editar ou excluir o treino
* ADMIN pode gerenciar qualquer treino
* SUPER_ADMIN possui acesso total