package com.projeto.financeiro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardResponse(
        @Schema(description = "Data inicial do periodo consultado", example = "2026-06-01")
        LocalDate periodoInicial,

        @Schema(description = "Data final do periodo consultado", example = "2026-06-30")
        LocalDate periodoFinal,

        @Schema(description = "Total de receitas no periodo", example = "5000.00")
        BigDecimal totalReceitas,

        @Schema(description = "Total de despesas no periodo", example = "1800.00")
        BigDecimal totalDespesas,

        @Schema(description = "Saldo do periodo (receitas - despesas)", example = "3200.00")
        BigDecimal saldo,

        @Schema(description = "Quantidade de titulos ativos do usuario", example = "8")
        int quantidadeTitulosAtivos,

        @Schema(description = "Quantidade de centros de custo cadastrados para o usuario", example = "4")
        int quantidadeCentrosDeCusto,

        @Schema(description = "Quantidade de faturamentos encontrados no periodo", example = "12")
        int quantidadeLancamentos,

        @Schema(description = "Serie temporal do fluxo de caixa no periodo")
        List<DashboardSerieResponse> serieFluxoCaixa,

        @Schema(description = "Lancamentos encontrados no periodo ordenados por vencimento")
        List<FaturamentoResponse> lancamentos
) {
}
