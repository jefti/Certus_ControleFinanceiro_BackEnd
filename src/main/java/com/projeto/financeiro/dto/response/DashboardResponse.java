package com.projeto.financeiro.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        BigDecimal totalAReceber,
        BigDecimal totalAPagar,
        BigDecimal totalRecebido,
        BigDecimal totalPago,
        BigDecimal saldo,
        Long quantidadeAtrasados,
        BigDecimal valorAtrasados,
        List<FaturamentoResponse> proximosVencimentos,
        List<CentroDeCustoValorResponse> centroDeCustoValor
) {
}
