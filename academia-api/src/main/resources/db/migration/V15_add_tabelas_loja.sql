CREATE TABLE produtos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    preco NUMERIC(10, 2) NOT NULL,
    estoque INTEGER NOT NULL,
    imagem_url VARCHAR(255)
);

CREATE TABLE pedidos (
    id BIGSERIAL PRIMARY KEY,
    aluno_id BIGINT NOT NULL,
    valor_total NUMERIC(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    metodo_pagamento VARCHAR(50) NOT NULL,

    CONSTRAINT fk_pedidos_aluno
        FOREIGN KEY (aluno_id)
        REFERENCES alunos(id)
);

CREATE TABLE itens_pedido (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(10, 2) NOT NULL,

    CONSTRAINT fk_itens_pedido_pedido
        FOREIGN KEY (pedido_id)
        REFERENCES pedidos(id),

    CONSTRAINT fk_itens_pedido_produto
        FOREIGN KEY (produto_id)
        REFERENCES produtos(id)
);

CREATE INDEX idx_pedidos_aluno_id
    ON pedidos(aluno_id);

CREATE INDEX idx_itens_pedido_pedido_id
    ON itens_pedido(pedido_id);

CREATE INDEX idx_itens_pedido_produto_id
    ON itens_pedido(produto_id);