-- Adiciona token_version para suportar revogacao de JWT.
-- O valor e embutido no token (claim "tv") e comparado a cada requisicao;
-- incrementa-lo (ex.: ao trocar a senha) invalida todos os tokens ja emitidos.

ALTER TABLE usuario ADD COLUMN token_version INTEGER NOT NULL DEFAULT 0;
