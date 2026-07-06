CREATE TABLE execucoes_treino
(
    id BIGSERIAL PRIMARY KEY,

    treino_id BIGINT NOT NULL,

    aluno_id BIGINT NOT NULL,

    data_execucao TIMESTAMP NOT NULL,

    concluido BOOLEAN NOT NULL,

    observacoes VARCHAR(500),

    CONSTRAINT fk_execucao_treino
        FOREIGN KEY (treino_id)
            REFERENCES treinos(id),

    CONSTRAINT fk_execucao_aluno
        FOREIGN KEY (aluno_id)
            REFERENCES alunos(id)
);