CREATE TABLE usuarios (
                          id BIGSERIAL PRIMARY KEY,

                          login VARCHAR(100) NOT NULL UNIQUE,

                          senha VARCHAR(255) NOT NULL,

                          role VARCHAR(20) NOT NULL,

                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);