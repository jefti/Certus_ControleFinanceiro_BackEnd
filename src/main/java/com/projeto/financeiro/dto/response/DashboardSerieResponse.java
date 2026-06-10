package com.projeto.financeiro.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardSerieResponse(
        @Schema(description = "Data do agrupamento no periodo", example = "2026-06-01")
        LocalDate data,

        @Schema(description = "Total de receitas para a data", example = "2500.00")
        BigDecimal totalReceitas,

        @Schema(description = "Total de despesas para a data", example = "1200.00")
        BigDecimal totalDespesas,

        @Schema(description = "Saldo do dia (receitas - despesas)", example = "1300.00")
        BigDecimal saldo
) {
}
