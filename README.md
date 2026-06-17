# 🏋️ Academia API

API REST desenvolvida com Spring Boot para gerenciamento de academias, com controle de alunos, planos, matrículas, personais, treinos e exercícios, listagens paginadas, autenticação via JWT e RBAC.

---

## 📑 Sumário

- [Tecnologias](#-tecnologias)
- [Arquitetura](#%EF%B8%8F-arquitetura)
- [Controle de Acesso (RBAC)](#-controle-de-acesso-rbac)
- [Como Executar](#%EF%B8%8F-como-executar)
- [Primeiro Acesso](#-primeiro-acesso)
- [Documentação Completa](#-documentação-completa)
- [Roadmap](#-roadmap)
- [Autor](#-autor)

---

## 🚀 Tecnologias

**Backend:** Java 21, Spring Boot 3, Spring Data JPA, Hibernate, Spring Validation, PostgreSQL, Flyway, MapStruct, Lombok, Maven

**Segurança:** Spring Security, JWT, BCrypt, RBAC

---

## 🏛️ Arquitetura

```text
Controller → DTO → Service → Mapper (MapStruct) → Entity → Repository → PostgreSQL
```

Tratamento de erros centralizado via **Global Exception Handler**, com exceptions customizadas (`ResourceNotFoundException`, `BusinessException`, `UnauthorizedException`) e validações padronizadas de DTOs.

---

## 👥 Controle de Acesso (RBAC)

| Role | Permissões resumidas |
| ---- | --------------------- |
| **SUPER_ADMIN** | Acesso total ao sistema |
| **ADMIN** | Gerencia alunos, planos, matrículas, personais e treinos |
| **PERSONAL** | Cria/edita/exclui apenas seus próprios treinos |
| **ALUNO** | Cria conta, faz login, visualiza seus dados e treinos |

📖 Detalhes completos das regras em [`docs/RBAC.md`](docs/RBAC.md)

---

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

---

## 🔑 Primeiro Acesso

```text
Login: superadmin
Senha: Academia@2026!
```

```http
POST /auth/login
```

Use o token retornado como `Authorization: Bearer SEU_TOKEN`.

---

## 📖 Documentação Completa

Para detalhes sobre entidades, relacionamentos, regras de negócio, endpoints e migrations Flyway, veja a pasta [`docs/`](docs):

- [`docs/ENTIDADES.md`](docs/ENTIDADES.md) — Estrutura do projeto, entidades e relacionamentos
- [`docs/REGRAS-NEGOCIO.md`](docs/REGRAS-NEGOCIO.md) — Regras de negócio
- [`docs/ENDPOINTS.md`](docs/ENDPOINTS.md) — Endpoints da API
- [`docs/ERROS.md`](docs/ERROS.md) — Tratamento de erros e exceptions
- [`docs/BANCO-DE-DADOS.md`](docs/BANCO-DE-DADOS.md) — Configuração e migrations Flyway

---

## 🧪 Roadmap

* ✅ Autenticação JWT, RBAC, Global Exception Handler, Validações, Paginação e Ordenação
* ⬜ Logs, Auditoria, Soft Delete
* ⬜ Swagger, Docker, CI/CD
* ⬜ Testes unitários e de integração

📖 Roadmap completo em [`docs/ROADMAP.md`](docs/ROADMAP.md)

---

## 👨‍💻 Autor

**Breno Rodrigues** — Desenvolvedor Full Stack
[GitHub](https://github.com/BrenoRodrigues05)