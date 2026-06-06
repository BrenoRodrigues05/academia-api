# 🏋️ Academia API

Uma API REST desenvolvida com Spring Boot para gerenciamento de academias, permitindo o controle de alunos, planos, matrículas, personais, treinos e exercícios de forma segura, organizada e escalável.

O projeto segue boas práticas de desenvolvimento, utilizando arquitetura em camadas, DTOs, MapStruct, Flyway para versionamento do banco de dados e PostgreSQL como banco relacional.

---

## 🚀 Tecnologias Utilizadas

### Backend

* Java 21
* Spring Boot
* Spring Data JPA
* Hibernate
* Spring Validation
* PostgreSQL
* Flyway
* MapStruct
* Lombok
* Maven

### Segurança (Em desenvolvimento)

* Spring Security
* JWT Authentication

---

## 📂 Estrutura do Projeto

```text
src/main/java/com/academia/academia_api

├── controllers
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
│   ├── Aluno
│   ├── Plano
│   ├── Matricula
│   ├── Personal
│   ├── Treino
│   ├── Exercicio
│   ├── ItemTreino
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

| Camada     | Responsabilidade          |
| ---------- | ------------------------- |
| Controller | Receber requisições HTTP  |
| DTO        | Transferência de dados    |
| Service    | Regras de negócio         |
| Mapper     | Conversão DTO ↔ Entity    |
| Repository | Acesso ao banco           |
| Entity     | Representação das tabelas |

---

# 📚 Entidades

## 👤 Aluno

Representa os alunos cadastrados na academia.

| Campo          | Tipo      |
| -------------- | --------- |
| id             | Long      |
| nome           | String    |
| email          | String    |
| dataNascimento | LocalDate |
| telefone       | String    |
| sexo           | SexoEnum  |

---

## 💳 Plano

Representa os planos disponíveis para contratação.

| Campo     | Tipo       |
| --------- | ---------- |
| id        | Long       |
| nome      | String     |
| descricao | String     |
| valor     | BigDecimal |
| tipo      | TipoPlano  |

---

## 📝 Matrícula

Representa o vínculo entre um aluno e um plano.

| Campo | Tipo    |
| ----- | ------- |
| id    | Long    |
| aluno | Aluno   |
| plano | Plano   |
| ativa | Boolean |

---

## 🏋️ Personal

Representa os profissionais responsáveis pela criação e gerenciamento dos treinos.

| Campo         | Tipo     |
| ------------- | -------- |
| id            | Long     |
| nome          | String   |
| sobrenome     | String   |
| email         | String   |
| telefone      | String   |
| cref          | String   |
| especialidade | String   |
| ativo         | Boolean  |
| sexo          | SexoEnum |

---

## 📋 Treino

Representa a ficha de treino atribuída a um aluno.

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

Representa um exercício disponível para utilização em treinos.

| Campo         | Tipo   |
| ------------- | ------ |
| id            | Long   |
| nome          | String |
| grupoMuscular | String |
| descricao     | String |

---

## 📝 ItemTreino

Representa um exercício dentro de um treino.

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
Aluno
   │
   └─────► Matrícula ◄─────┐
                           │
                           ▼
                         Plano


Personal
    │
    │ cria
    ▼
 Treino
    │
    │ pertence
    ▼
  Aluno


Treino
   │
   │ 1:N
   ▼
ItemTreino
   │
   │ N:1
   ▼
Exercicio
```

---

# 📌 Regras de Negócio

## Matrículas

* Um aluno só pode possuir uma matrícula ativa.
* Não é permitido criar matrícula para aluno inexistente.
* Não é permitido criar matrícula para plano inexistente.
* Matrículas são desativadas ao invés de removidas fisicamente.

---

## Planos

* Não permite busca com ID inválido.
* Não permite valores negativos.
* Permite busca por:

  * Nome
  * Tipo
  * Descrição
  * Valor

---

## Alunos

* Campos obrigatórios são validados.
* Utilização de DTOs para entrada e saída de dados.

---

## Personais

* E-mail único.
* CREF único.
* Não permite cadastro duplicado.
* Controle de status ativo/inativo.
* Não permite exclusão de personal ativo.

---

## Treinos

* Um aluno pode possuir apenas um treino ativo.
* Todo treino deve possuir um personal responsável.
* Todo treino deve possuir um aluno associado.
* Controle de ativação/desativação.
* Futuramente:

  * Apenas o personal criador poderá editar.
  * Apenas o personal criador poderá excluir.
  * SuperAdmin poderá sobrescrever permissões.

---

## Exercícios

* Catálogo reutilizável.
* Busca por nome.
* Busca por grupo muscular.

---

## Itens de Treino

* Todo item deve estar associado a um treino.
* Todo item deve estar associado a um exercício.
* Um exercício não pode ser repetido no mesmo treino.
* Controle de séries.
* Controle de repetições.
* Controle de descanso.

---

# 📋 Endpoints

## 👤 Alunos

```http
GET    /api/alunos
GET    /api/alunos/{id}
GET    /api/alunos/busca-nome
GET    /api/alunos/busca-email
GET    /api/alunos/busca-sexo
GET    /api/alunos/busca-idade

POST   /api/alunos
PUT    /api/alunos/{id}
DELETE /api/alunos/{id}
```

---

## 💳 Planos

```http
GET    /api/planos
GET    /api/planos/{id}

GET    /api/planos/busca-nome
GET    /api/planos/busca-valor
GET    /api/planos/busca-descricao
GET    /api/planos/busca-tipo

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

## 🏋️ Personais

```http
GET    /api/personais
GET    /api/personais/{id}

GET    /api/personais/busca-email
GET    /api/personais/busca-cref
GET    /api/personais/busca-nome

GET    /api/personais/ativos
GET    /api/personais/inativos

POST   /api/personais
PUT    /api/personais/{id}
PATCH  /api/personais/{id}/status
DELETE /api/personais/{id}
```

---

## 📋 Treinos

```http
GET    /api/treinos
GET    /api/treinos/{id}

GET    /api/treinos/aluno/{alunoId}
GET    /api/treinos/personal/{personalId}

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

GET    /api/exercicios/busca-nome
GET    /api/exercicios/grupo-muscular

POST   /api/exercicios
PUT    /api/exercicios/{id}
DELETE /api/exercicios/{id}
```

---

## 📝 Itens de Treino

```http
GET    /api/itens-treino
GET    /api/itens-treino/{id}

GET    /api/itens-treino/treino/{treinoId}
GET    /api/itens-treino/exercicio/{exercicioId}

POST   /api/itens-treino
PUT    /api/itens-treino/{id}
DELETE /api/itens-treino/{id}
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

PersonalCreateDTO
PersonalUpdateDTO
PersonalResponseDTO

TreinoCreateDTO
TreinoUpdateDTO
TreinoResponseDTO

ExercicioCreateDTO
ExercicioUpdateDTO
ExercicioResponseDTO

ItemTreinoCreateDTO
ItemTreinoUpdateDTO
ItemTreinoResponseDTO
```

---

# 🔄 MapStruct

Utilizado para conversão automática entre DTOs e entidades.

### Exemplo

```java
@Mapper(componentModel = "spring")
public interface PersonalMapper {

    PersonalResponseDTO toResponseDTO(Personal personal);

    Personal toEntity(PersonalCreateDTO dto);

    void updateEntityFromDTO(
            PersonalUpdateDTO dto,
            @MappingTarget Personal personal);
}
```

---

# 🛢️ Banco de Dados

## PostgreSQL

### Configuração

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/academia_db
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
V1__create_alunos_planos_matriculas.sql
V2__create_personais.sql
V3__create_treinos.sql
V4__create_exercicios.sql
V5__create_itens_treino.sql
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

* Spring Security
* JWT Authentication
* Controle de acesso baseado em roles
* Rotas protegidas
* Autenticação via Bearer Token
* Controle de permissões por Personal
* SuperAdmin

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

* [ ] Spring Security
* [ ] JWT
* [ ] SuperAdmin
* [ ] Controle de permissões por Personal
* [ ] Histórico de treinos
* [ ] Evolução de carga/peso
* [ ] Upload de imagens dos exercícios
* [ ] Swagger/OpenAPI
* [ ] Docker
* [ ] Testes Unitários
* [ ] Testes de Integração
* [ ] Global Exception Handler
* [ ] Paginação
* [ ] Filtros Dinâmicos
* [ ] Logs Centralizados

---

# 📈 Evolução Técnica Aplicada

Durante o desenvolvimento foram aplicados conceitos como:

* Arquitetura em Camadas
* DTO Pattern
* Repository Pattern
* Service Layer Pattern
* MapStruct
* Injeção de Dependência
* Validação de Dados
* Boas Práticas REST
* Tratamento de Regras de Negócio
* Versionamento de Banco com Flyway

---

# 👨‍💻 Autor

## Breno Rodrigues

Desenvolvedor Full Stack

GitHub:

https://github.com/BrenoRodrigues05

---

## ⭐ Projeto em evolução

Este projeto está sendo desenvolvido com foco em aprendizado contínuo e aplicação de boas práticas utilizadas em projetos Java/Spring Boot do mercado.
