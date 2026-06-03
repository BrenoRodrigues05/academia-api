# 🏋️ Academia API

Uma API REST desenvolvida com Spring Boot para gerenciamento de academias, permitindo o controle de alunos, planos e matrículas de forma segura, organizada e escalável.

O projeto segue boas práticas de desenvolvimento, utilizando arquitetura em camadas, DTOs, MapStruct, Flyway para versionamento do banco de dados e PostgreSQL como banco relacional.

---

## 🚀 Tecnologias Utilizadas

### Backend

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- Spring Validation
- PostgreSQL
- Flyway
- MapStruct
- Lombok
- Maven

### Segurança (Em desenvolvimento)

- Spring Security
- JWT Authentication

---

## 📂 Estrutura do Projeto

```text
src/main/java/com/academia/academia_api

├── controllers
│
├── DTOs
│
├── entity
│   ├── Aluno
│   ├── Plano
│   ├── Matricula
│   └── enums
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

### Responsabilidades

| Camada | Responsabilidade |
|----------|----------|
| Controller | Receber requisições HTTP |
| DTO | Transferência de dados |
| Service | Regras de negócio |
| Mapper | Conversão DTO ↔ Entity |
| Repository | Acesso ao banco |
| Entity | Representação das tabelas |

---

# 📚 Entidades

## 👤 Aluno

Representa os alunos cadastrados na academia.

### Campos

| Campo | Tipo |
|----------|----------|
| id | Long |
| nome | String |
| email | String |
| dataNascimento | LocalDate |
| telefone | String |
| sexo | SexoEnum |

---

## 💳 Plano

Representa os planos disponíveis para contratação.

### Campos

| Campo | Tipo |
|----------|----------|
| id | Long |
| nome | String |
| descricao | String |
| valor | BigDecimal |
| tipo | TipoPlano |

---

## 📝 Matrícula

Representa o vínculo entre um aluno e um plano.

### Campos

| Campo | Tipo |
|----------|----------|
| id | Long |
| aluno | Aluno |
| plano | Plano |
| ativa | Boolean |

---

# 🔄 Relacionamentos

```text
Aluno
   │
   └─────► Matrícula ◄─────┐
                           │
                           ▼
                         Plano
```

---

# 📌 Regras de Negócio

## Matrículas

- Um aluno só pode possuir uma matrícula ativa.
- Não é permitido criar matrícula para aluno inexistente.
- Não é permitido criar matrícula para plano inexistente.
- Matrículas são desativadas ao invés de removidas fisicamente.

## Planos

- Não permite busca com ID inválido.
- Não permite valores negativos.
- Permite busca por:
  - Nome
  - Tipo
  - Descrição
  - Valor

## Alunos

- Campos obrigatórios são validados.
- Utilização de DTOs para entrada e saída de dados.

---

# 📋 Endpoints

---

## 👤 Alunos

### Listar alunos

```http
GET /api/alunos
```

### Buscar aluno por ID

```http
GET /api/alunos/{id}
```

### Criar aluno

```http
POST /api/alunos
```

### Atualizar aluno

```http
PUT /api/alunos/{id}
```

### Excluir aluno

```http
DELETE /api/alunos/{id}
```

---

## 💳 Planos

### Listar planos

```http
GET /api/planos
```

### Buscar por ID

```http
GET /api/planos/{id}
```

### Buscar por nome

```http
GET /api/planos/busca-nome?nome=Premium
```

### Buscar por valor

```http
GET /api/planos/busca-valor?valor=129.90
```

### Buscar por descrição

```http
GET /api/planos/busca-descricao?descricao=Musculacao
```

### Buscar por tipo

```http
GET /api/planos/busca-tipo?tipo=MENSAL
```

### Criar plano

```http
POST /api/planos
```

### Atualizar plano

```http
PUT /api/planos/{id}
```

### Remover plano

```http
DELETE /api/planos/{id}
```

---

## 📝 Matrículas

### Listar matrículas

```http
GET /api/matriculas
```

### Buscar matrícula por ID

```http
GET /api/matriculas/{id}
```

### Criar matrícula

```http
POST /api/matriculas
```

### Desativar matrícula

```http
DELETE /api/matriculas/{id}
```

---

# 📦 DTOs

O projeto utiliza DTOs para desacoplar as entidades da API.

### Exemplos

```text
AlunoCreateDTO
AlunoUpdateDTO
AlunoResponseDTO

PlanoCreateDTO
PlanoUpdateDTO
PlanoResponseDTO

MatriculaCreateDTO
MatriculaResponseDTO
```

---

# 🔄 MapStruct

Utilizado para conversão automática entre DTOs e entidades.

### Exemplo

```java
@Mapper(componentModel = "spring")
public interface PlanoMapper {

    PlanoResponseDTO toResponseDTO(Plano plano);

    Plano toEntity(PlanoCreateDTO dto);

    void updateEntityFromDTO(
            PlanoUpdateDTO dto,
            @MappingTarget Plano plano);
}
```

---

# 🛢️ Banco de Dados

## PostgreSQL

### Configuração

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/academia
spring.datasource.username=postgres
spring.datasource.password=senha

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
```

---

# 🛫 Flyway

O projeto utiliza Flyway para versionamento do banco de dados.

### Estrutura

```text
src/main/resources/db/migration
```

Exemplo:

```text
V1__create_alunos.sql
V2__create_planos.sql
V3__create_matriculas.sql
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

# 🔒 Segurança (Roadmap)

Em desenvolvimento:

- Spring Security
- JWT Authentication
- Controle de acesso baseado em roles
- Rotas protegidas
- Autenticação via Bearer Token

Fluxo planejado:

```text
Login
  ↓
JWT Token
  ↓
Bearer Token
  ↓
Rotas Protegidas
```

---

# 🧪 Melhorias Futuras

- [ ] Spring Security
- [ ] JWT
- [ ] Swagger/OpenAPI
- [ ] Docker
- [ ] Testes Unitários
- [ ] Testes de Integração
- [ ] Global Exception Handler
- [ ] Paginação
- [ ] Filtros Dinâmicos
- [ ] Logs Centralizados

---

# 📈 Evolução Técnica Aplicada

Durante o desenvolvimento foram aplicados conceitos como:

- Arquitetura em Camadas
- DTO Pattern
- Repository Pattern
- Service Layer Pattern
- MapStruct
- Injeção de Dependência
- Validação de Dados
- Boas Práticas REST
- Tratamento de Regras de Negócio
- Versionamento de Banco com Flyway

---

# 👨‍💻 Autor

## Breno Rodrigues

Desenvolvedor Backend Java

GitHub:

:contentReference[oaicite:0]{index=0}

---

## ⭐ Projeto em evolução

Este projeto está sendo desenvolvido com foco em aprendizado contínuo e aplicação de boas práticas utilizadas em projetos Java/Spring Boot do mercado.
