-- Remove coluna data_pagamento da tabela titulo

ALTER TABLE titulo DROP COLUMN IF EXISTS data_pagamento;
