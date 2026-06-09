ALTER TABLE alunos
    ADD COLUMN usuario_id BIGINT;

ALTER TABLE alunos
    ADD CONSTRAINT fk_aluno_usuario
        FOREIGN KEY (usuario_id)
            REFERENCES usuarios(id);

ALTER TABLE alunos
    ADD CONSTRAINT uk_aluno_usuario
        UNIQUE (usuario_id);