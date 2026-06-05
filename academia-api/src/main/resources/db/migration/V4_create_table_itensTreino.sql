CREATE TABLE itens_treino (
                              id BIGSERIAL PRIMARY KEY,

                              treino_id BIGINT NOT NULL,
                              exercicio_id BIGINT NOT NULL,

                              series INTEGER NOT NULL,
                              repeticoes INTEGER NOT NULL,
                              descanso_segundos INTEGER,

                              CONSTRAINT fk_item_treino
                                  FOREIGN KEY (treino_id)
                                      REFERENCES treinos(id)
                                      ON DELETE CASCADE,

                              CONSTRAINT fk_item_exercicio
                                  FOREIGN KEY (exercicio_id)
                                      REFERENCES exercicios(id)
);