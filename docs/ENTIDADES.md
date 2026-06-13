# 📚 Entidades e Relacionamentos

## 👤 Usuarios

Responsável pela autenticação do sistema.

| Campo     | Tipo          |
| --------- | ------------- |
| id        | Long          |
| login     | String        |
| senha     | String        |
| role      | UserRole      |
| ativo     | Boolean       |
| createdAt | LocalDateTime |

---

## 👤 Aluno

| Campo          | Tipo      |
| -------------- | --------- |
| id             | Long      |
| nome           | String    |
| email          | String    |
| telefone       | String    |
| dataNascimento | LocalDate |
| sexo           | SexoEnum  |
| usuario        | Usuarios  |

---

## 🏋️ Personal

| Campo   | Tipo     |
| ------- | -------- |
| id      | Long     |
| nome    | String   |
| email   | String   |
| cref    | String   |
| usuario | Usuarios |

---

## 💳 Plano

| Campo     | Tipo       |
| --------- | ---------- |
| id        | Long       |
| nome      | String     |
| descricao | String     |
| valor     | BigDecimal |
| tipo      | TipoPlano  |

---

## 📝 Matrícula

| Campo | Tipo    |
| ----- | ------- |
| id    | Long    |
| aluno | Aluno   |
| plano | Plano   |
| ativa | Boolean |

---

## 📋 Treino

| Campo       | Tipo     |
| ----------- | -------- |
| id          | Long     |
| nome        | String   |
| observacoes | String   |
| ativo       | Boolean  |
| personal    | Personal |
| aluno       | Aluno    |

---

## 💪 Exercício

| Campo         | Tipo   |
| ------------- | ------ |
| id            | Long   |
| nome          | String |
| grupoMuscular | String |
| descricao     | String |

---

## 🏋️ ItemTreino

| Campo            | Tipo      |
| ---------------- | --------- |
| id               | Long      |
| series           | Integer   |
| repeticoes       | Integer   |
| descansoSegundos | Integer   |
| treino           | Treino    |
| exercicio        | Exercicio |

---

# 🔄 Relacionamentos

```text
Usuarios
   │
   ├────────► Aluno
   │
   └────────► Personal

Aluno
   │
   └─────► Matricula ◄───── Plano

Personal
   │
   └─────► Treino ◄───── Aluno

Treino
   │
   └─────► ItemTreino ◄───── Exercicio
```

---

# 📂 Estrutura do Projeto

```text
src/main/java/com/academia/academia_api

├── controllers
│   ├── AuthController
│   ├── AlunoController
│   ├── PlanoController
│   ├── MatriculaController
│   ├── PersonalController
│   ├── TreinoController
│   ├── ExercicioController
│   └── ItemTreinoController
│
├── DTOs
│
├── entity
│   ├── Usuarios
│   ├── Aluno
│   ├── Plano
│   ├── Matricula
│   ├── Personal
│   ├── Treino
│   ├── Exercicio
│   ├── ItemTreino
│   └── enums
│       ├── UserRole
│       ├── SexoEnum
│       └── TipoPlano
│
├── infra
│   ├── security
│   │   ├── SecurityConfigurations
│   │   ├── SecurityFilter
│   │   └── TokenService
│   │
│   └── exceptions
│
├── mappings
│
├── repository
│
├── services
│
└── AcademiaApiApplication
```