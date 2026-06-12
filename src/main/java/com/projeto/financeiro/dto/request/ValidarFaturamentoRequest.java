package com.projeto.financeiro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ValidarFaturamentoRequest(
        @Schema(description = "Data e hora em que o faturamento foi pago ou recebido. Se nao for informada, o backend usa a data/hora atual.", example = "2026-06-02T10:00:00", nullable = true)
        LocalDateTime dataPagamento,

        @Schema(description = "Observacao opcional sobre a validacao do faturamento", example = "Pago via Pix", nullable = true)
        @Size(max = 500) String observacao
) {
}
