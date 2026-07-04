# 🏋️ Academia API

![CI](https://github.com/BrenoRodrigues05/academia-api/actions/workflows/ci.yml/badge.svg)

API REST desenvolvida com Spring Boot para gerenciamento de academias, com controle de alunos, planos, matrículas, personais, treinos e exercícios, listagens paginadas, autenticação via JWT e RBAC.

## 📑 Sumário

- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Controle de Acesso (RBAC)](#-controle-de-acesso-rbac)
- [Como Executar](#-como-executar)
- [Primeiro Acesso](#-primeiro-acesso)
- [Testes Unitários](#-testes-unitários)
- [Documentação da API](#-documentação-da-api)
- [Roadmap](#️-roadmap)
- [Autor](#-autor)

## 🚀 Tecnologias

**Backend:** Java 21, Spring Boot 3, Spring Data JPA, Hibernate, Spring Validation, PostgreSQL, Flyway, MapStruct, Lombok, Maven

**Segurança:** Spring Security, JWT, BCrypt, RBAC

**DevOps & Containers:** Docker, Docker Compose

**Documentação:** SpringDoc OpenAPI 3, Swagger UI

## 🏛️ Arquitetura

```
Controller → DTO → Service → Mapper (MapStruct) → Entity → Repository → PostgreSQL
```

* **Tratamento de Erros:** Centralizado via Global Exception Handler, com exceptions customizadas (`ResourceNotFoundException`, `BusinessException`, `UnauthorizedException`) e validações padronizadas de DTOs.
* **Auditoria de Dados:** Implementada de forma global através de uma `@MappedSuperclass` (`BaseEntity`) integrada ao **Spring Data JPA Auditing**, garantindo rastreabilidade automatizada de criação e modificação (`created_at`, `updated_at`, `created_by`, `updated_by`) em todas as entidades principais.

## 👥 Controle de Acesso (RBAC)

| Role | Permissões resumidas |
|---|---|
| `SUPER_ADMIN` | Acesso total ao sistema |
| `ADMIN` | Gerencia alunos, planos, matrículas, personais e treinos |
| `PERSONAL` | Cria/edita/exclui apenas seus próprios treinos |
| `ALUNO` | Cria conta, faz login, visualiza seus dados e treinos |

📖 Detalhes completos das regras em [`docs/RBAC.md`](docs/RBAC.md)

## ⚙️ Como Executar

### Pré-requisitos
- [Git](https://git-scm.com/)
- [Docker e Docker Compose](https://www.docker.com/) (para a forma padrão de execução)
- Java 21 e Maven (apenas se for rodar localmente sem Docker)

### Clonando o projeto

```bash
git clone https://github.com/BrenoRodrigues05/academia-api.git
cd academia-api/academia-api
```

### 🐳 Executando com Docker (Forma Padrão — Recomendada)

Esta é a forma mais rápida de subir o ambiente completo (API + banco PostgreSQL), sem precisar instalar ou configurar o Postgres manualmente. Na raiz do projeto, execute:

```bash
docker compose up -d --build
```

### 🗄️ Ambiente de Banco de Dados e pgAdmin

O projeto inclui o **pgAdmin 4** pré-configurado no Docker Compose para que você possa gerenciar e visualizar as tabelas do banco de dados graficamente de forma simples.

#### 1. Acesso ao pgAdmin
Após subir os containers com o comando do Docker, abra o navegador e acesse:
* **URL:** http://localhost:5050
* **E-mail de Login:** `admin@academia.com`
* **Senha de Login:** `admin123`

#### 2. Conectando ao Banco (Passo a Passo)
1. No painel esquerdo do pgAdmin, clique com o botão direito em **Servers** ➔ **Register** ➔ **Server...**
2. Na aba **General**, defina o nome da conexão como: `AcademiaDB`
3. Na aba **Connection**, preencha exatamente com estes dados:
    * **Host name/address:** `postgres` *(nome do serviço configurado no docker)*
    * **Port:** `5432`
    * **Maintenance database:** `academia_db`
    * **Username:** `postgres`
    * **Password:** `postgres`
4. Marque a caixinha **Save Password** para não precisar digitar a senha novamente.
5. Clique em **Save**.

#### 📊 Estrutura de Tabelas Geradas
Navegando por `Servers ➔ AcademiaDB ➔ Databases ➔ academia_db ➔ Schemas ➔ public ➔ Tables`, você poderá visualizar e inspecionar as tabelas criadas automaticamente via Flyway:
* `usuarios` | `alunos` | `personais` | `planos` | `matriculas` | `treinos` | `itens_treino` | `exercicios` | `flyway_schema_history`


O Docker Compose já cria o container do PostgreSQL e sobe a API conectada a ele automaticamente. A API estará disponível em `http://localhost:8080`.

### 🖥️ Executando localmente (sem Docker)

Se preferir rodar a aplicação diretamente na sua máquina:

1. Tenha um PostgreSQL instalado e rodando localmente.
2. Crie o banco de dados:

```sql
CREATE DATABASE academia_db;
```

3. Configure as credenciais de acesso (usuário, senha, URL) em `application.properties`.
4. Compile e execute a aplicação:

```bash
mvn clean install
mvn spring-boot:run
```

## 🔑 Primeiro Acesso

| Campo | Valor |
|---|---|
| Endpoint | `POST /auth/login` |
| Login | `superadmin` |
| Senha | `Academia@2026!` |

Use o token retornado como `Authorization: Bearer SEU_TOKEN`.

## 🧪 Testes Unitários

O projeto conta com testes automatizados desenvolvidos com JUnit 5 e Mockito para validação das regras de negócio do sistema (como a estrutura de treinos).

Para rodar toda a suíte de testes de maneira limpa, isolada e segura, utilize o comando:

```bash
mvn clean test
```

## 📖 Documentação da API

A documentação interativa está disponível via Swagger UI após subir a aplicação:

| | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

O acesso à documentação é público (não requer autenticação). Para testar endpoints protegidos diretamente pelo Swagger, clique em **Authorize** e informe o token JWT obtido no login.

---

Para detalhes sobre entidades, relacionamentos, regras de negócio, endpoints e migrations Flyway, veja a pasta `docs/`:

- [`docs/ENTIDADES.md`](docs/ENTIDADES.md) — Estrutura do projeto, entidades e relacionamentos
- [`docs/REGRAS-NEGOCIO.md`](docs/REGRAS-NEGOCIO.md) — Regras de negócio
- [`docs/ENDPOINTS.md`](docs/ENDPOINTS.md) — Endpoints da API
- [`docs/ERROS.md`](docs/ERROS.md) — Tratamento de erros e exceptions
- [`docs/BANCO-DE-DADOS.md`](docs/BANCO-DE-DADOS.md) — Configuração e migrations Flyway

## 🗺️ Roadmap

- ✅ Autenticação JWT, RBAC, Global Exception Handler, Validações, Paginação e Ordenação
- ✅ Swagger / OpenAPI 3
- ✅ Auditoria de Dados (Spring Data JPA Auditing com BaseEntity)
- ✅ Testes unitários
- ✅ Dockerization (API & PostgreSQL via Docker Compose)
- ✅ CI/CD Pipeline
- ⬜ Logs, Soft Delete

📖 Roadmap completo em [`docs/ROADMAP.md`](docs/ROADMAP.md)

## 👨‍💻 Autor

**Breno Rodrigues** — Desenvolvedor Full Stack · [Linkedin](https://www.linkedin.com/in/brenorodrigues05/)
