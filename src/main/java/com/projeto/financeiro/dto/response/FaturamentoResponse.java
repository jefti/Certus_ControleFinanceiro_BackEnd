package com.projeto.financeiro.dto.response;

import com.projeto.financeiro.entity.enums.StatusTitulo;
import com.projeto.financeiro.entity.enums.TipoTitulo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FaturamentoResponse(
        Long id,
        Long tituloId,
        String tituloDescricao,
        TipoTitulo tipo,
        LocalDate dataVencimento,
        BigDecimal valor,
        LocalDateTime dataPagamento,
        StatusTitulo status,
        String observacao
) {
}
