ALTER TABLE usuario
    ALTER COLUMN data_criacao TYPE TIMESTAMPTZ USING (data_criacao AT TIME ZONE 'UTC'),
    ALTER COLUMN data_inativacao TYPE TIMESTAMPTZ USING (data_inativacao AT TIME ZONE 'UTC');

