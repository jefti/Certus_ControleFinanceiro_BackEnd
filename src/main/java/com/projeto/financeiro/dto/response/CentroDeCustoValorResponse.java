package com.projeto.financeiro.dto.response;

import java.math.BigDecimal;

public record CentroDeCustoValorResponse(
        String centroCusto,
        BigDecimal valor
) { }

/*
Foi criado para só passar qual Centro de Custo e o valor, e não detalhar.
Usado para a Dashboard.

O valor e a SOMA do valor CHEIO dos faturamentos a pagar em aberto de cada
centro de custo. Se um titulo estiver vinculado a varios centros de custo,
o valor integral e contabilizado em cada um deles (sem rateio).

Centro de Custo "Moradia" (entidade cadastrada)
  - Título "Aluguel" = R$ 2.200,00
  - Título "Condomínio" = R$ 450,00
  - Título "Internet" = R$ 100,00

Dashboard mostra:
{
  "centroCusto": "Moradia",
  "valor": 2750.00  - SOMA dos títulos daquele centro de custo
}
 */