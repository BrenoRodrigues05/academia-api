# 🏋️ Academia API

API REST desenvolvida com Spring Boot para gerenciamento de academias, permitindo o controle de alunos, planos, matrículas, personais, treinos, exercícios e autenticação de usuários com JWT.

O projeto segue boas práticas de desenvolvimento, utilizando arquitetura em camadas, DTOs, MapStruct, Flyway para versionamento do banco de dados, PostgreSQL como banco relacional e Spring Security para autenticação e autorização.

---

# 🚀 Tecnologias Utilizadas

## Backend

* Java 21
* Spring Boot 3
* Spring Data JPA
* Hibernate
* Spring Validation
* PostgreSQL
* Flyway
* MapStruct
* Lombok
* Maven

## Segurança

* Spring Security
* JWT (JSON Web Token)
* BCrypt Password Encoder
* Role Based Access Control (RBAC)

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
│   └── security
│
├── mappings
│
├── repository
│
├── services
│
└── AcademiaApiApplication
```

---

# 🏛️ Arquitetura

```text
Controller
    ↓
DTO
    ↓
Service
    ↓
Mapper (MapStruct)
    ↓
Entity
    ↓
Repository
    ↓
PostgreSQL
```

## Responsabilidades

| Camada     | Responsabilidade          |
| ---------- | ------------------------- |
| Controller | Receber requisições HTTP  |
| DTO        | Transferência de dados    |
| Service    | Regras de negócio         |
| Mapper     | Conversão DTO ↔ Entity    |
| Repository | Acesso ao banco           |
| Entity     | Representação das tabelas |

---

# 🔐 Autenticação e Segurança

A API utiliza autenticação baseada em JWT.

Fluxo:

```text
Login
  ↓
JWT Token
  ↓
Bearer Token
  ↓
Rotas Protegidas
```

Senha dos usuários:

```text
BCrypt
```

As senhas nunca são armazenadas em texto puro.

---

# 👥 Controle de Acesso (RBAC)

O sistema utiliza Roles para controle de acesso.

## SUPER_ADMIN

Possui acesso total ao sistema.

Pode:

* Criar administradores
* Criar personais
* Gerenciar todos os recursos
* Editar ou remover qualquer treino

---

## ADMIN

Pode:

* Gerenciar alunos
* Gerenciar planos
* Gerenciar matrículas
* Gerenciar personais
* Visualizar e administrar treinos

---

## PERSONAL

Pode:

* Criar treinos
* Editar treinos criados por ele
* Remover treinos criados por ele
* Gerenciar itens de treino

---

## ALUNO

Pode:

* Criar sua própria conta
* Realizar login
* Visualizar seus próprios dados
* Visualizar seus próprios treinos

---

# 📚 Entidades

## 👤 Usuários

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

# 📌 Regras de Negócio

## Usuários

* Login único.
* Senha criptografada com BCrypt.
* Controle por Roles.
* Usuários podem ser ativados ou desativados.

---

## Matrículas

* Um aluno só pode possuir uma matrícula ativa.
* Não é permitido criar matrícula para aluno inexistente.
* Não é permitido criar matrícula para plano inexistente.
* Matrículas são desativadas ao invés de removidas fisicamente.

---

## Personais

* E-mail único.
* CREF único.
* Relacionamento com usuário do sistema.

---

## Treinos

* Um aluno pode possuir apenas um treino ativo por vez.
* Todo treino deve possuir um personal responsável.
* O personal é obtido através do usuário autenticado.
* Apenas o personal criador pode editar ou excluir seu treino.
* Administradores podem gerenciar qualquer treino.
* Super administradores possuem acesso total.

---

# 📋 Endpoints Principais

## 🔐 Autenticação

### Login

```http
POST /auth/login
```

### Dados do usuário autenticado

```http
GET /auth/me
```

### Registro de Aluno

```http
POST /auth/register
```

### Registro de Personal

```http
POST /auth/register/personal
```

### Registro de Admin

```http
POST /auth/register/admin
```

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

---

# 🛢️ Banco de Dados

## PostgreSQL

Configuração:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/academia_db
spring.datasource.username=postgres
spring.datasource.password=senha

spring.jpa.hibernate.ddl-auto=validate
```

---

# 🛫 Flyway

Versionamento do banco de dados através de migrations.

Exemplos:

```text
V1__MigracaoInicial.sql
V2__create_table_personais.sql
V3__create_table_exercicios.sql
V4__create_table_itensTreino.sql
V5__create_table_treinos.sql
V6__create_usuarios.sql
V7__add_ativo_to_usuarios.sql
V8__add_usuario_id_to_alunos.sql
V9__add_usuario_id_to_personais.sql
```

---

# ⚙️ Como Executar

## Clonar projeto

```bash
git clone https://github.com/BrenoRodrigues05/academia-api.git
```

## Entrar na pasta

```bash
cd academia-api
```

## Compilar

```bash
mvn clean install
```

## Executar

```bash
mvn spring-boot:run
```

---

# 🔑 Primeiro Acesso

O sistema cria automaticamente um usuário SUPER_ADMIN.

```text
Login: superadmin
Senha: Academia@2026!
```

Após realizar login:

```http
POST /auth/login
```

será retornado um JWT válido para utilização nas rotas protegidas.

---

# 🧪 Roadmap

* ✅ Spring Security
* ✅ JWT Authentication
* ✅ Controle de acesso por Roles
* ✅ Cadastro de Alunos
* ✅ Cadastro de Personais
* ✅ Cadastro de Administradores
* ✅ Usuário autenticado (/auth/me)
* ✅ Autorização por proprietário do treino
* ⬜ Refresh Token
* ⬜ Swagger/OpenAPI
* ⬜ Docker
* ⬜ Testes Unitários
* ⬜ Testes de Integração
* ⬜ Global Exception Handler
* ⬜ Paginação
* ⬜ Logs Centralizados
* ⬜ Histórico de Treinos
* ⬜ Evolução de Carga/Peso

---

# 👨‍💻 Autor

## Breno Rodrigues

Desenvolvedor Full Stack

GitHub:
https://github.com/BrenoRodrigues05

---

## ⭐ Projeto em evolução

Projeto desenvolvido com foco em aprendizado contínuo, arquitetura limpa, boas práticas REST, segurança com JWT e padrões utilizados em aplicações Spring Boot utilizadas no mercado.
