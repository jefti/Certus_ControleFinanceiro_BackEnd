package com.projeto.financeiro.dto.response;

import com.projeto.financeiro.entity.enums.Recorrencia;
import com.projeto.financeiro.entity.enums.TipoTitulo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public record TituloResponse(
        Long id,
        String descricao,
        BigDecimal valor,
        LocalDate dataVencimento,
        TipoTitulo tipo,
        Recorrencia recorrencia,
        LocalDate dataFim,
        boolean ativo,
        int quantidadeFaturamentos,
        List<CentroDeCustoResponse> centrosDeCusto
) {
}
