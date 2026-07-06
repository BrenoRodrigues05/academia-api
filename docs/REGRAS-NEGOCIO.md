# 📌 Regras de Negócio

## Usuários

* Login único
* Senha criptografada com BCrypt
* Controle por Roles
* Usuários podem ser ativados ou desativados

---

## Matrículas

* Um aluno só pode possuir uma matrícula ativa
* Não é permitido criar matrícula para aluno inexistente
* Não é permitido criar matrícula para plano inexistente
* Matrículas são desativadas ao invés de removidas fisicamente

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
