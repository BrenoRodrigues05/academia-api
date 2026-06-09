INSERT INTO usuarios (
    login,
    senha,
    role,
    ativo,
    created_at
)
SELECT
    'superadmin',
    '$2a$10$log3/CCpwNODDkWGQiKoK.IMSB2/nUZI1zfOhSQgE4aZCzu3YZlt',
    'SUPER_ADMIN',
    true,
    CURRENT_TIMESTAMP
    WHERE NOT EXISTS (
    SELECT 1
    FROM usuarios
    WHERE login = 'superadmin'
);