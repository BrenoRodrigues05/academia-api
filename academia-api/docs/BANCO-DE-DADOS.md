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