package com.projeto.financeiro.dto.response;

import com.projeto.financeiro.entity.enums.TipoTitulo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


public record TituloResponse(
        Long id,
        String descricao,
        BigDecimal valor,
        LocalDate dataVencimento,
        LocalDateTime dataPagamento,
        TipoTitulo tipo
        // todo: list centro de custo response
) {
}

