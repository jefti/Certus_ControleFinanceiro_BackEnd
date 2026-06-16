-- Adiciona contador de tentativas ao codigo de recuperacao de senha.
-- Protege contra brute force do codigo de 6 digitos: apos N tentativas
-- incorretas o pedido e invalidado (ver RecuperacaoSenhaService).

ALTER TABLE recuperacao_senha ADD COLUMN tentativas INTEGER NOT NULL DEFAULT 0;