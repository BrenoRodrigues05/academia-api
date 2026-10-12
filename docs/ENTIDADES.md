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
| execucoes | List&lt;ExecucaoTreino&gt; | Relacionamento OneToMany (Mapeado por `aluno`) |
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
| pagamentos | List&lt;Pagamento&gt; | Relacionamento OneToMany (Mapeado por `matricula`) |
| *Herda de BaseEntity* | - | Campos de auditoria |

---

## 💰 Pagamento

| Campo | Tipo | Observação |
| :--- | :--- | :--- |
| **id** | Long | Chave Primária |
| **matricula** | Matricula | Relacionamento N:1 (Chave Estrangeira: `matricula_id`) |
| **valor** | BigDecimal | Valor cobrado no pagamento |
| **status** | StatusPagamentoEnum | Status da transação [PENDENTE, PAGO, CANCELADO, EXPIRADO, REEMBOLSADO] |
| **gatewayId** | String | Identificador no gateway de pagamento |
| **codigoPix** | String | Código Pix Copia e Cola (`TEXT`) |
| **qrCodeBase64** | String | Imagem do QR Code em Base64 (`TEXT`) |
| **dataCriacao** | LocalDateTime | Data e hora de geração do pagamento |
| **pagamento** | LocalDateTime | Data e hora em que o pagamento foi confirmado |
| **expiracao** | LocalDateTime | Data e hora limite para pagamento Pix |
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
| execucoes | List&lt;ExecucaoTreino&gt; | Relacionamento OneToMany (Mapeado por `treino`) |
| itensTreino | List&lt;ItensTreino&gt; | Relacionamento OneToMany (Mapeado por `treino`) |
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

## 🏋️ Execução Treino

| Campo | Tipo | Observação |
| :--- | :--- | :--- |
| **id** | Long | Chave Primária (Auto-incremento) |
| **treino** | Treino | Relacionamento ManyToOne (Chave Estrangeira: `treino_id`) |
| **aluno** | Aluno | Relacionamento ManyToOne (Chave Estrangeira: `aluno_id`) |
| **dataExecucao** | LocalDateTime | Data e hora em que o treino foi realizado |
| **concluido** | Boolean | Status de conclusão do treino |
| **observacoes** | String | Notas adicionais (Máx: 500 caracteres) |

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

## 🛍️ Produto

Itens disponíveis na loja da academia. Tabela: `produtos`.

| Campo | Tipo | Observação |
| :--- | :--- | :--- |
| id | Long | Chave Primária (`BIGSERIAL`) |
| nome | String | Obrigatório (`VARCHAR(255)`) |
| descricao | String | Opcional (`TEXT`) |
| preco | BigDecimal | Obrigatório (`NUMERIC(10,2)`) |
| estoque | Integer | Quantidade disponível. Obrigatório |
| imagemUrl | String | URL da imagem do produto (`VARCHAR(255)`, coluna `imagem_url`) |
| *Herda de BaseEntity* | - | Campos de auditoria |
 
---

## 🧾 Pedido

Compra de produtos realizada por um aluno. Tabela: `pedidos`.

| Campo | Tipo | Observação |
| :--- | :--- | :--- |
| id | Long | Chave Primária (`BIGSERIAL`) |
| aluno | Aluno | Relacionamento N:1 (Chave Estrangeira: `aluno_id`) |
| valorTotal | BigDecimal | Valor total do pedido (`NUMERIC(10,2)`, coluna `valor_total`) |
| status | StatusPedidoEnum | Status do pedido |
| metodoPagamento | MetodoPagamentoLojaEnum | Forma de pagamento escolhida (coluna `metodo_pagamento`) |
| itens | List<ItemPedido> | Relacionamento OneToMany (Mapeado por `pedido`) |
| *Herda de BaseEntity* | - | Campos de auditoria |
 
---

## 🛒 ItemPedido

Entidade associativa que compõe os produtos de um pedido. Tabela: `itens_pedido`.

| Campo | Tipo | Observação |
| :--- | :--- | :--- |
| id | Long | Chave Primária (`BIGSERIAL`) |
| pedido | Pedido | Relacionamento N:1 (Chave Estrangeira: `pedido_id`) |
| produto | Produto | Relacionamento N:1 (Chave Estrangeira: `produto_id`) |
| quantidade | Integer | Quantidade comprada |
| precoUnitario | BigDecimal | Preço do produto no momento da compra (`NUMERIC(10,2)`, coluna `preco_unitario`) |
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
                │
                └─────► Pagamento

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
│   ├── Pagamento
│   ├── Plano
│   ├── ItemPedido
│   ├── Pedido
│   ├── Produto
│   ├── Matricula
│   ├── Personal
│   ├── Treino
|   ├── ExecucaoTreino
│   ├── Exercicio
│   ├── ItemTreino
│   └── enums
│       ├── UserRole
│       ├── SexoEnum
│       ├── GrupoMuscular 
│       ├── MetodoPagamentEnum
│       ├── StatusPagamentoEnum
│       ├── StatusPedidoEnum
│       ├── MetodoPagamentoLojaEnum
│       └── TipoPlano
│
│
├── gateway
│   └── PixGateway
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