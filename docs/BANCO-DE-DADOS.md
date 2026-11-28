# 🛢️ Banco de Dados & Configurações

## 🐘 PostgreSQL Configuration

As configurações do banco de dados e integrações são definidas via `application.properties` utilizando variáveis de ambiente para credenciais sensíveis:

```properties
# Conexão com o Banco de Dados
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/academia_db}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}

# Hibernate & JPA (Validação com Flyway)
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Security & JWT (sem valor padrão: a aplicação não sobe sem a variável)
api.security.token.secret=${JWT_SECRET}

# Mercado Pago Gateway
mercadopago.access-token=${MERCADOPAGO_ACCESS_TOKEN}

# E-mail (SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME:}
spring.mail.password=${MAIL_PASSWORD:}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000
app.mail.from=${MAIL_USERNAME:noreply@academia.local}
app.mail.nome-academia=Minha Academia

# Observabilidade & Health Check
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=always
```

> ⚠️ Em `${...}` vai o **nome** da variável de ambiente, nunca o valor da credencial. `JWT_SECRET` e `MERCADOPAGO_ACCESS_TOKEN` não possuem valor padrão de propósito: se faltarem, a aplicação falha ao subir em vez de rodar com uma chave fraca.

### 🔐 Variáveis de ambiente

| Variável | Obrigatória | Descrição |
| :--- | :---: | :--- |
| `JWT_SECRET` | ✅ | Chave de assinatura dos tokens JWT |
| `MERCADOPAGO_ACCESS_TOKEN` | ✅ | Access token do Mercado Pago |
| `MAIL_USERNAME` | ✅ | Conta SMTP usada como remetente |
| `MAIL_PASSWORD` | ✅ | Senha SMTP (no Gmail, use uma senha de app) |
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | ❌ | Conexão com o banco (padrão: Postgres local) |

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
V15_add_tabelas_loja.sql
V16_add_vencimento_matricula.sql
V17_add_dataBloqueio_matriculas.sql
```

* **V16:** adiciona os campos de vencimento e ciclo de cobrança na matrícula.
* **V17:** adiciona `data_bloqueio` em `matriculas`, usada para contar o prazo de 1 mês até a inativação.