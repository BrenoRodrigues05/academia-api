ALTER TABLE matriculas
    ADD COLUMN plano_agendado_id BIGINT,
    ADD COLUMN status VARCHAR(255),
    ADD COLUMN dia_vencimento INTEGER,
    ADD COLUMN data_vencimento DATE;

ALTER TABLE matriculas
    ADD CONSTRAINT fk_matriculas_plano_agendado
        FOREIGN KEY (plano_agendado_id)
            REFERENCES planos (plano);

ALTER TABLE matriculas
    ADD CONSTRAINT chk_matriculas_dia_vencimento
        CHECK (dia_vencimento BETWEEN 1 AND 25);