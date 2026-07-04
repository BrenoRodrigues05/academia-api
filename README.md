# 🏋️ Academia API

API REST desenvolvida com Spring Boot para gerenciamento de academias, com controle de alunos, planos, matrículas, personais, treinos e exercícios, listagens paginadas, autenticação via JWT e RBAC.

## 📑 Sumário

- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Controle de Acesso (RBAC)](#-controle-de-acesso-rbac)
- [Como Executar](#-como-executar)
- [Primeiro Acesso](#-primeiro-acesso)
- [Documentação da API](#-documentação-da-api)
- [Roadmap](#-roadmap)
- [Autor](#-autor)

## 🚀 Tecnologias

**Backend:** Java 21, Spring Boot 3, Spring Data JPA, Hibernate, Spring Validation, PostgreSQL, Flyway, MapStruct, Lombok, Maven

**Segurança:** Spring Security, JWT, BCrypt, RBAC

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

```bash
git clone https://github.com/BrenoRodrigues05/academia-api.git
cd academia-api
```

Crie o banco PostgreSQL:

```sql
CREATE DATABASE academia_db;
```

Configure as credenciais em `application.properties`, depois:

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

## 🧪 Roadmap

- ✅ Autenticação JWT, RBAC, Global Exception Handler, Validações, Paginação e Ordenação
- ✅ Swagger / OpenAPI 3
- ✅ Auditoria de Dados (Spring Data JPA Auditing com BaseEntity)
- ⬜ Logs, Soft Delete
- ⬜ Docker, CI/CD
- ⬜ Testes unitários e de integração

📖 Roadmap completo em [`docs/ROADMAP.md`](docs/ROADMAP.md)

## 👨‍💻 Autor

**Breno Rodrigues** — Desenvolvedor Full Stack · [Linkedin](https://www.linkedin.com/in/brenorodrigues05/)
