CREATE TABLE treinos (
                         id BIGSERIAL PRIMARY KEY,
                         nome VARCHAR(100) NOT NULL,
                         observacoes VARCHAR(500),
                         ativo BOOLEAN NOT NULL DEFAULT TRUE,

                         personal_id BIGINT NOT NULL,
                         aluno_id BIGINT NOT NULL UNIQUE,

                         CONSTRAINT fk_treino_personal
                             FOREIGN KEY (personal_id)
                                 REFERENCES personais(id),

                         CONSTRAINT fk_treino_aluno
                             FOREIGN KEY (aluno_id)
                                 REFERENCES alunos(id)
);