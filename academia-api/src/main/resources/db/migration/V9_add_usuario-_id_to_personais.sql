ALTER TABLE personais
    ADD COLUMN usuario_id BIGINT;

ALTER TABLE personais
    ADD CONSTRAINT fk_personal_usuario
        FOREIGN KEY (usuario_id)
            REFERENCES usuarios(id);

ALTER TABLE personais
    ADD CONSTRAINT uk_personal_usuario
        UNIQUE (usuario_id);