package com.projeto.financeiro.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FaturamentoRequest(
        @NotNull @DecimalMin(value = "0.01", message = "valor deve ser maior que zero") BigDecimal valor,
        @NotNull LocalDate dataVencimento,
        @Size(max = 500) String observacao
) {
}
