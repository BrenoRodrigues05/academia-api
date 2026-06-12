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
POST   /api/alunos
PUT    /api/alunos/{id}
DELETE /api/alunos/{id}
```

---

## 🏋️ Personais

```http
GET    /api/personais
GET    /api/personais/{id}
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
DELETE /api/matriculas/{id}
```

---

## 📋 Treinos

```http
GET    /api/treinos
GET    /api/treinos/{id}
POST   /api/treinos
PUT    /api/treinos/{id}
PATCH  /api/treinos/{id}/status
DELETE /api/treinos/{id}
```

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
POST   /api/exercicios
PUT    /api/exercicios/{id}
DELETE /api/exercicios/{id}
```

---

## 🏋️ Itens de Treino

```http
GET    /api/itens-treino
GET    /api/itens-treino/{id}
POST   /api/itens-treino
PUT    /api/itens-treino/{id}
DELETE /api/itens-treino/{id}
```