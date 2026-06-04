CREATE TABLE personais (
                           id BIGSERIAL PRIMARY KEY,
                           nome VARCHAR(100) NOT NULL,
                           sobrenome VARCHAR(100) NOT NULL,
                           email VARCHAR(100) NOT NULL UNIQUE,
                           telefone VARCHAR(15) NOT NULL,
                           cref VARCHAR(20) NOT NULL UNIQUE,
                           especialidade VARCHAR(100),
                           ativo BOOLEAN NOT NULL DEFAULT TRUE,
                           sexo VARCHAR(20) NOT NULL,

                           CONSTRAINT chk_personais_sexo
                               CHECK (sexo IN ('MASCULINO', 'FEMININO'))
);