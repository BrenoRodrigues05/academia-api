# 📋 Endpoints da API

## 🔐 Autenticação

### Login

```http
POST /auth/login
```

Retorna:

```json
{
  "token": "jwt_token"
}
```

---

### Usuário autenticado

```http
GET /auth/me
```

Retorna os dados do usuário logado.

---

### Registro de Aluno

```http
POST /auth/register
```

Acesso público.

Cria:

* Usuário (ROLE_ALUNO)
* Aluno

---

### Registro de Personal

```http
POST /auth/register/personal
```

Acesso: `SUPER_ADMIN`

Cria:

* Usuário (ROLE_PERSONAL)
* Personal

---

### Registro de Administrador

```http
POST /auth/register/admin
```

Acesso: `SUPER_ADMIN`

Cria:

* Usuário (ROLE_ADMIN)

---

## 👤 Alunos

```http
GET    /api/alunos
GET    /api/alunos/{id}
GET    /api/alunos/busca-nome
GET    /api/alunos/busca-email
GET    /api/alunos/busca-sexo
GET    /api/alunos/busca-idade
POST   /api/alunos
PUT    /api/alunos/{id}
DELETE /api/alunos/{id}
```

---

## 🏋️ Personais

```http
GET    /api/personais
GET    /api/personais/{id}
GET    /api/personais/busca-email
GET    /api/personais/busca-cref
GET    /api/personais/busca-nome
GET    /api/personais/ativos
GET    /api/personais/inativos
POST   /api/personais
PUT    /api/personais/{id}
PATCH  /api/personais/{id}/status
DELETE /api/personais/{id}
```

---

## 💳 Planos

```http
GET    /api/planos
GET    /api/planos/{id}
GET    /api/planos/busca-nome
GET    /api/planos/busca-valor
GET    /api/planos/busca-descricao
GET    /api/planos/busca-tipo
POST   /api/planos
PUT    /api/planos/{id}
DELETE /api/planos/{id}
```

---

## 📝 Matrículas

```http
GET    /api/matriculas
GET    /api/matriculas/{id}
POST   /api/matriculas
PATCH  /api/matriculas/{id}/desativar
```

---

## 📋 Treinos

```http
GET    /api/treinos
GET    /api/treinos/{id}
GET    /api/treinos/ativos
GET    /api/treinos/inativos
GET    /api/treinos/aluno/{id}/historico
GET    /api/treinos/me
GET    /api/treinos/me/historico
GET    /api/treinos/busca-nome
GET    /api/treinos/personal/{personalId}
GET    /api/treinos/aluno/{alunoId}
POST   /api/treinos
PUT    /api/treinos/{id}
PATCH  /api/treinos/{id}/status
DELETE /api/treinos/{id}
```

---

## 📋 Execuções de treinos

```http
POST    /api/execucoes/iniciar/{treinoId}

### Regra de Segurança dos Treinos

```text
SUPER_ADMIN -> acesso total
ADMIN       -> acesso total
PERSONAL    -> apenas treinos criados por ele
ALUNO       -> apenas consulta
```

---

## 💪 Exercícios

```http
GET    /api/exercicios
GET    /api/exercicios/{id}
GET    /api/exercicios/busca-nome
GET    /api/exercicios/grupo-muscular
POST   /api/exercicios
PUT    /api/exercicios/{id}
DELETE /api/exercicios/{id}
```

---

## 🏋️ Itens de Treino

```http
GET    /api/itens-treino
GET    /api/itens-treino/{id}
GET    /api/itens-treino/treino/{treinoId}
GET    /api/itens-treino/exercicio/{exercicioId}
POST   /api/itens-treino
PUT    /api/itens-treino/{id}
DELETE /api/itens-treino/{id}
```
