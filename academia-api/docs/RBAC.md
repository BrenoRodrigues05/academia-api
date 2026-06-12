# 👥 Controle de Acesso (RBAC)

O sistema utiliza Roles para controle de acesso.

## SUPER_ADMIN

Possui acesso total ao sistema.

Permissões:

* Criar Administradores
* Criar Personais
* Gerenciar qualquer entidade
* Editar qualquer treino
* Excluir qualquer treino
* Gerenciar usuários

---

## ADMIN

Permissões:

* Gerenciar alunos
* Gerenciar planos
* Gerenciar matrículas
* Gerenciar personais
* Gerenciar treinos

---

## PERSONAL

Permissões:

* Criar treinos
* Editar apenas os próprios treinos
* Excluir apenas os próprios treinos
* Gerenciar itens de treino

---

## ALUNO

Permissões:

* Criar sua própria conta
* Realizar login
* Visualizar seus dados
* Visualizar seus treinos

---

# 🔐 Autenticação e Segurança

A API utiliza autenticação baseada em JWT.

Fluxo de autenticação:

```text
Login
   ↓
Validação das credenciais
   ↓
Geração do JWT
   ↓
Bearer Token
   ↓
Acesso às rotas protegidas
```

As senhas são criptografadas utilizando BCrypt (`BCryptPasswordEncoder`). Nenhuma senha é armazenada em texto puro.