# 🏋️ Academia API

API REST desenvolvida com Spring Boot para gerenciamento de academias, permitindo o controle de alunos, planos, matrículas, personais, treinos, exercícios e autenticação de usuários com JWT.

O projeto segue boas práticas de desenvolvimento utilizando arquitetura em camadas, DTOs, MapStruct, Flyway para versionamento do banco de dados, PostgreSQL como banco relacional, Spring Security para autenticação e autorização e JWT para controle de acesso stateless.

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

As senhas são criptografadas utilizando BCrypt.

```text
BCryptPasswordEncoder
```

Nenhuma senha é armazenada em texto puro.

---

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

# 📚 Entidades

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
* Todo treino deve possuir um personal responsável
* O personal é obtido automaticamente pelo usuário autenticado
* Apenas o personal criador pode editar ou excluir o treino
* ADMIN pode gerenciar qualquer treino
* SUPER_ADMIN possui acesso total

---

# 🔐 Endpoints de Autenticação

## Login

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

## Usuário autenticado

```http
GET /auth/me
```

Retorna os dados do usuário logado.

---

## Registro de Aluno

```http
POST /auth/register
```

Acesso público.

Cria:

* Usuário (ROLE_ALUNO)
* Aluno

---

## Registro de Personal

```http
POST /auth/register/personal
```

Acesso:

```text
SUPER_ADMIN
```

Cria:

* Usuário (ROLE_PERSONAL)
* Personal

---

## Registro de Administrador

```http
POST /auth/register/admin
```

Acesso:

```text
SUPER_ADMIN
```

Cria:

* Usuário (ROLE_ADMIN)

---

# 📋 Endpoints Principais

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

ADMIN -> acesso total

PERSONAL -> apenas treinos criados por ele

ALUNO -> apenas consulta
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

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/academia_db
spring.datasource.username=postgres
spring.datasource.password=senha

spring.jpa.hibernate.ddl-auto=validate
```

---

# 🛫 Flyway

Versionamento do banco de dados através de migrations.

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

## Clonar o projeto

```bash
git clone https://github.com/BrenoRodrigues05/academia-api.git
```

## Entrar na pasta

```bash
cd academia-api
```

## Configurar o banco

Crie um banco PostgreSQL:

```sql
CREATE DATABASE academia_db;
```

Configure as credenciais no:

```properties
application.properties
```

---

## Compilar

```bash
mvn clean install
```

---

## Executar

```bash
mvn spring-boot:run
```

---

# 🔑 Primeiro Acesso

O sistema possui um SUPER_ADMIN inicial.

```text
Login: superadmin
Senha: Academia@2026!
```

Gerar token:

```http
POST /auth/login
```

Resposta:

```json
{
  "token": "jwt_token"
}
```

Utilize:

```http
Authorization: Bearer SEU_TOKEN
```

nas rotas protegidas.

---

# 🧪 Roadmap

## Segurança

* ✅ Spring Security
* ✅ JWT Authentication
* ✅ Controle por Roles
* ✅ Endpoint /auth/me
* ✅ Registro de Alunos
* ✅ Registro de Personais
* ✅ Registro de Administradores
* ✅ Permissão por proprietário do treino

## Backend

* ⬜ Global Exception Handler
* ⬜ Paginação
* ⬜ Logs centralizados
* ⬜ Auditoria de entidades
* ⬜ Soft Delete

## DevOps

* ⬜ Swagger/OpenAPI
* ⬜ Docker
* ⬜ Docker Compose
* ⬜ CI/CD

## Qualidade

* ⬜ Testes Unitários
* ⬜ Testes de Integração
* ⬜ Testcontainers

## Funcionalidades Futuras

* ⬜ Histórico de treinos
* ⬜ Evolução de carga/peso
* ⬜ Upload de imagens dos exercícios
* ⬜ Refresh Token
* ⬜ Recuperação de senha

---

# 👨‍💻 Autor

## Breno Rodrigues

Desenvolvedor Full Stack

GitHub:

https://github.com/BrenoRodrigues05

---

# ⭐ Projeto em Evolução

Projeto desenvolvido com foco em aprendizado contínuo, arquitetura limpa, boas práticas REST, segurança com JWT, Spring Security, controle de acesso baseado em Roles e padrões amplamente utilizados em aplicações corporativas Spring Boot.
