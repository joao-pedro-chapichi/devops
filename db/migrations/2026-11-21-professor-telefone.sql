-- Execute uma vez no PostgreSQL existente antes de iniciar a versão atualizada.
-- O telefone antes era INTEGER e não comportava números brasileiros de 10 ou 11 dígitos.
ALTER TABLE professor
    ALTER COLUMN telefone_professor TYPE varchar(20)
    USING telefone_professor::varchar(20);

ALTER TABLE professor
    ALTER COLUMN graduacao_professor TYPE varchar(50);
