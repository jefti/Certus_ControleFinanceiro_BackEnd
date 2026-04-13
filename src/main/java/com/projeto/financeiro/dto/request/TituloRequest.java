package com.projeto.financeiro.dto.request;

import com.projeto.financeiro.entity.enums.TipoTitulo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;



public record TituloRequest(
        Long id,
        String descricao,
        BigDecimal valor,
        LocalDate dataVencimento,
        LocalDateTime dataPagamento,
        TipoTitulo tipo
        // todo: list centro de custo request
) {
}

