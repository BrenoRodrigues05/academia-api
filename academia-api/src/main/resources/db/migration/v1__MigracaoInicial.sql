CREATE TABLE planos (
                        id BIGSERIAL PRIMARY KEY,
                        nome VARCHAR(100) NOT NULL,
                        descricao VARCHAR(500) NOT NULL,
                        valor NUMERIC(6, 2) NOT NULL,
                        tipo VARCHAR(50) NOT NULL,
                        imagem_url VARCHAR(255)
);

CREATE TABLE alunos (
                        id BIGSERIAL PRIMARY KEY,
                        nome VARCHAR(100) NOT NULL,
                        email VARCHAR(100) NOT NULL,
                        data_nascimento DATE,
                        telefone VARCHAR(15) NOT NULL,
                        sexo VARCHAR(20) NOT NULL
);

CREATE TABLE matriculas (
                            matricula BIGSERIAL PRIMARY KEY,
                            plano_id BIGINT NOT NULL,
                            aluno_id BIGINT NOT NULL,
                            ativa BOOLEAN NOT NULL DEFAULT TRUE,

                            CONSTRAINT fk_matriculas_plano FOREIGN KEY (plano_id) REFERENCES planos(id),
                            CONSTRAINT fk_matriculas_aluno FOREIGN KEY (aluno_id) REFERENCES alunos(id)
);