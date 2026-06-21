-- Adiciona o papel de autorizacao do usuario (USER / ADMIN).
-- Todo usuario nasce como USER; a API publica NUNCA define o papel.
-- A promocao a ADMIN e feita apenas no bootstrap (ADMIN_EMAILS) no startup.

ALTER TABLE usuario ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';
