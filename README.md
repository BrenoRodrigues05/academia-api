Aqui está o arquivo completo, pronto no bloco abaixo para você copiar tudo de uma vez clicando no botão de copiar:

```markdown
# 🏋️ Academia API

Uma API REST desenvolvida com Spring Boot para gerenciamento de academias, permitindo o controle de alunos, planos, matrículas, personais, treinos e exercícios de forma segura, organizada e escalável.

O projeto segue boas práticas de desenvolvimento, utilizando arquitetura em camadas, DTOs, MapStruct, Flyway para versionamento do banco de dados e PostgreSQL como banco relacional.

---

## 🚀 Tecnologias Utilizadas

### Backend & Segurança

* Java 21
* Spring Boot 3
* Spring Security (Autenticação e Autorização)
* JWT (JSON Web Token)
* Spring Data JPA
* Hibernate
* Spring Validation
* PostgreSQL
* Flyway
* MapStruct
* Lombok
* Maven

---

## 📂 Estrutura do Projeto

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
│   ├── Usuario
│   ├── Aluno
│   ├── Plano
│   ├── Matricula
│   ├── Personal
│   ├── Treino
│   ├── Exercicio
│   ├── ItemTreino
│   └── enums
│       ├── Role (SUPER_ADMIN, ADMIN, PERSONAL, ALUNO)
│       └── SexoEnum
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

## 🏛️ Arquitetura

A aplicação segue uma arquitetura em camadas:

```text
Controller (Filtros JWT / Security)
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

### Responsabilidades

| Camada | Responsabilidade |
| --- | --- |
| Controller | Receber requisições HTTP e validar tokens/roles |
| DTO | Transferência de dados |
| Service | Regras de negócio |
| Mapper | Conversão DTO ↔ Entity |
| Repository | Acesso ao banco |
| Entity | Representação das tabelas |

---

# 📚 Novas Entidades de Controle & Segurança

## 🔐 Usuário

Representa as credenciais de acesso ao sistema e define o nível de permissão (Role).

| Campo | Tipo |
| --- | --- |
| id | Long |
| login | String (Único) |
| senha | String (Criptografada com BCrypt) |
| role | Role (Enum) |

---

# 🔄 Relacionamentos

```text
   ┌──────────────────┐
   │     Usuário      │
   └─┬──────────────┬─┘
     │ 1:1          │ 1:1
     ▼              ▼
   Aluno         Personal
     │              │
     ├─► Matrícula  │ cria
     │      │       ▼
     │      ▼    Treino ◄─────┘
     ▼    Plano     │
   Aluno ◄──────────┘ pertence
     │
     │ 1:N
     ▼
 ItemTreino ◄───► Exercicio (N:1)

```

---

# 📌 Regras de Negócio & Segurança

## 🔒 Controle de Acesso (Roles)

A API implementa controle de acesso baseado em perfis de usuários (`Role-Based Access Control - RBAC`):

* **SUPER_ADMIN**: Possui acesso total ao sistema, gerenciamento de administradores e configurações globais. O sistema inicializa automaticamente um usuário mestre na primeira execução para garantir o acesso inicial.
* **ADMIN**: Gerencia o funcionamento da academia (Alunos, Personais, Planos e Matrículas).
* **PERSONAL**: Possui permissão para criar, editar e gerenciar treinos e itens de treino para os alunos.
* **ALUNO**: Perfil com acesso de leitura para visualizar seus próprios treinos, dados cadastrais e status da matrícula.

## 👤 Vínculos de Usuários

* Todo **Aluno** criado pode possuir uma conta de **Usuário** vinculada (1:1) com a role `ALUNO` para acessar o sistema.
* Todo **Personal** criado pode possuir uma conta de **Usuário** vinculada (1:1) com a role `PERSONAL`.

---

## Matrículas

* Um aluno só pode possuir uma matrícula ativa.
* Não é permitido criar matrícula para aluno ou plano inexistente.
* Matrículas são desativadas ao invés de removidas fisicamente.

## Personais

* E-mail e CREF únicos.
* Não permite exclusão de personal ativo.

## Treinos

* Um aluno pode possuir apenas um treino ativo por vez.
* Todo treino deve possuir um personal responsável e um aluno associado.

---

# 📋 Endpoints Principais

## 🔐 Autenticação

```http
POST   /api/auth/login    -> Retorna o Bearer JWT Token válido

```

## 👤 Alunos

```http
GET    /api/alunos
GET    /api/alunos/{id}
POST   /api/alunos
PUT    /api/alunos/{id}
DELETE /api/alunos/{id}

```

## 💳 Planos

```http
GET    /api/planos
POST   /api/planos
PUT    /api/planos/{id}
DELETE /api/planos/{id}

```

## 📝 Matrículas

```http
GET    /api/matriculas
POST   /api/matriculas
DELETE /api/matriculas/{id}

```

## 🏋️ Personais

```http
GET    /api/personais
POST   /api/personais
PUT    /api/personais/{id}
PATCH  /api/personais/{id}/status
DELETE /api/personais/{id}

```

## 📋 Treinos

```http
GET    /api/treinos
POST   /api/treinos
PUT    /api/treinos/{id}
PATCH  /api/treinos/{id}/status

```

---

# 🛢️ Banco de Dados & Versionamento (Flyway)

O projeto utiliza **Flyway** para estruturar e migrar o banco de dados evolutivamente.

### Estrutura de Migrations

```text
V1__create_alunos_planos_matriculas.sql
V2__create_personais.sql
V3__create_treinos.sql
V4__create_exercicios.sql
V5__create_itens_treino.sql
V6__create_usuarios_and_security_setup.sql

```

---

# ⚙️ Como Executar

## 1. Clonar projeto

```bash
git clone [https://github.com/BrenoRodrigues05/academia-api.git](https://github.com/BrenoRodrigues05/academia-api.git)
cd academia-api

```

## 2. Configurar Banco de Dados

Ajuste as credenciais do PostgreSQL em `src/main/resources/application.properties`.

## 3. Compilar e Executar

```bash
mvn clean install
mvn spring-boot:run

```

## 4. Primeiro Acesso (Credenciais do SuperAdmin)

Após rodar o projeto pela primeira vez, o banco de dados será populado automaticamente com o primeiro usuário de acesso total. Utilize o endpoint `/api/auth/login` com a requisição contendo as seguintes credenciais para gerar o seu Bearer Token JWT:

* **Login:** `superadmin`
* **Senha:** `Academia@2026!`

Com o token gerado, inclua-o no cabeçalho `Authorization` das requisições subsequentes para liberar o cadastro de novos usuários, `ADMIN`, `PERSONAL` ou `ALUNO`.

---

# 🧪 Roadmap de Desenvolvimento

* ✅ Spring Security configurado
* ✅ Autenticação via JWT Token
* ✅ Controle de acesso baseado em Roles (`SUPER_ADMIN`, `ADMIN`, `PERSONAL`, `ALUNO`)
* ✅ Relacionamentos Usuário ↔ Aluno e Usuário ↔ Personal
* ✅ Inicialização automática do SuperAdmin padrão
* [ ] Histórico de treinos do aluno
* [ ] Evolução de carga/peso
* [ ] Upload de imagens dos exercícios
* [ ] Documentação interativa com Swagger/OpenAPI
* [ ] Dockerização do projeto (Dockerfile e docker-compose)
* [ ] Testes Unitários e de Integração
* [ ] Global Exception Handler customizado

---

# 👨‍💻 Autor

## Breno Rodrigues

Desenvolvedor Full Stack

GitHub: [BrenoRodrigues05](https://github.com/BrenoRodrigues05)

```

```
