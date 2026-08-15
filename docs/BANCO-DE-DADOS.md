# 🛢️ Banco de Dados & Configurações

## 🐘 PostgreSQL Configuration

As configurações do banco de dados e integrações são definidas via `application.properties` utilizando variáveis de ambiente para credenciais sensíveis:

```properties
# Conexão com o Banco de Dados
spring.datasource.url=jdbc:postgresql://localhost:5432/academia_db
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD:postgres}

# Hibernate & JPA (Validação com Flyway)
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Security & JWT
api.security.token.secret=${JWT_SECRET:minha-chave-secreta-de-desenvolvimento}

# Mercado Pago Gateway
mercadopago.access-token=${MERCADOPAGO_ACCESS_TOKEN:SEU_ACCESS_TOKEN_DO_MERCADO_PAGO}

# Observabilidade & Health Check
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=always
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
V10_add_auditoria.sql
V11_add-datas-treino.sql
V12_create_table_execucoes_treino.sql
V13_create_table_pagamentos.sql
V14_add_metodoPagamento_to_Pagamento.sql
```
