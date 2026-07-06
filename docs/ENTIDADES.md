# 📚 Entidades e Relacionamentos

Todas as entidades principais herdam de `BaseEntity` para fins de auditoria, ganhando automaticamente os campos de rastreabilidade.

## 📝 Auditoria Global (BaseEntity)
Estes campos estão presentes em **todas** as tabelas do sistema, gerenciados automaticamente pelo Spring Data JPA Auditing:

| Campo | Tipo | Descrição |
| :--- | :--- | :--- |
| **createdAt** | LocalDateTime | Data/hora de criação do registro |
| **updatedAt** | LocalDateTime | Data/hora da última modificação |
| **createdBy** | String | Usuário responsável pela criação |
| **updatedBy** | String | Usuário responsável pela última modificação |

## 👤 Usuarios

Responsável pela autenticação e controle de acesso do sistema.

| Campo | Tipo | Observação |
| :--- | :--- | :--- |
| id | Long | Chave Primária |
| login | String | Único / Username |
| senha | String | Hash BCrypt |
| role | UserRole | [SUPER_ADMIN, ADMIN, PERSONAL, ALUNO] |
| ativo | Boolean | Controle de bloqueio |
| *Herda de BaseEntity* | - | Campos de auditoria |

---

## 👤 Aluno

| Campo | Tipo | Observação |
| :--- | :--- | :--- |
| id | Long | Chave Primária |
| nome | String | |
| email | String | Único |
| telefone | String | |
| dataNascimento | LocalDate | |
| sexo | SexoEnum | [MASCULINO, FEMININO] |
| usuario | Usuarios | Relacionamento 1:1 |
| *Herda de BaseEntity* | - | Campos de auditoria |

---

## 🏋️ Personal

| Campo | Tipo | Observação |
| :--- | :--- | :--- |
| id | Long | Chave Primária |
| nome | String | |
| email | String | Único |
| cref | String | Registro profissional único |
| usuario | Usuarios | Relacionamento 1:1 |
| *Herda de BaseEntity* | - | Campos de auditoria |

---

## 💳 Plano

| Campo | Tipo | Observação |
| :--- | :--- | :--- |
| id | Long | Chave Primária |
| nome | String | Ex: Mensal, Semestral |
| descricao | String | |
| valor | BigDecimal | |
| tipo | TipoPlano | Ex: [MUSCULACAO, CROSSFIT, COMPLETO] |
| *Herda de BaseEntity* | - | Campos de auditoria |

---

## 📝 Matrícula

| Campo | Tipo | Observação |
| :--- | :--- | :--- |
| id | Long | Chave Primária |
| aluno | Aluno | Relacionamento N:1 |
| plano | Plano | Relacionamento N:1 |
| ativa | Boolean | Status do plano do aluno |
| *Herda de BaseEntity* | - | Campos de auditoria |

---

## 📋 Treino

| Campo | Tipo | Observação |
| :--- | :--- | :--- |
| id | Long | Chave Primária |
| nome | String | Ex: Treino A - Hipertrofia |
| observacoes | String | Dicas gerais de execução |
| ativo | Boolean | |
| dataInicio | LocalDate |
| dataFim | LocalDate |
| personal | Personal | Relacionamento N:1 (Criador) |
| aluno | Aluno | Relacionamento N:1 (Destinatário) |
| *Herda de BaseEntity* | - | Campos de auditoria |

---

## 💪 Exercício

| Campo | Tipo | Observação |
| :--- | :--- | :--- |
| id | Long | Chave Primária |
| nome | String | Ex: Supino Reto |
| grupoMuscular | String | Ex: Peito |
| descricao | String | Link de vídeo ou instruções |
| *Herda de BaseEntity* | - | Campos de auditoria |

---

## 🏋️ ItemTreino

Entidade associativa que compõe a ficha de treinos.

| Campo | Tipo | Observação |
| :--- | :--- | :--- |
| id | Long | Chave Primária |
| series | Integer | |
| repeticoes | Integer | |
| descansoSegundos | Integer | Tempo em segundos |
| treino | Treino | Relacionamento N:1 |
| exercicio | Exercicio | Relacionamento N:1 |
| *Herda de BaseEntity* | - | Campos de auditoria |

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
│   ├── BaseEntity         
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
|   ├── config
|   |   ├── AuditorAwareImpl
|   |   ├── JpaAuditingConfig
|   |   └──  OpenApiConfig
|   | 
│   ├── security
|   |   ├── DataLoader
│   │   ├── SecurityConfigurations
│   │   ├── SecurityFilter
│   │   └── TokenService
│   │
│   └── exceptions
|        ├── BadRequestException
|        ├── ErrorResponse
|        ├── ForbiddenException
|        ├── GlobalExceptionHandler
|        ├── ResourceNotFoundException
|        └── ValidationErrorResponse
│
├── mappings
│
├── repository
│
├── services
│
└── AcademiaApiApplication
```
