package com.projeto.financeiro.dto.response;

import com.projeto.financeiro.entity.enums.StatusTitulo;
import com.projeto.financeiro.entity.enums.TipoTitulo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FaturamentoResponse(
        @Schema(description = "Id do faturamento", example = "14")
        Long id,

        @Schema(description = "Id do titulo ao qual o faturamento pertence", example = "4")
        Long tituloId,

        @Schema(description = "Descricao do titulo vinculado ao faturamento", example = "Aluguel")
        String tituloDescricao,

        @Schema(description = "Tipo da movimentacao financeira", example = "PAGAR")
        TipoTitulo tipo,

        @Schema(description = "Data de vencimento da ocorrencia", example = "2026-06-01")
        LocalDate dataVencimento,

        @Schema(description = "Valor da ocorrencia", example = "1200.00")
        BigDecimal valor,

        @Schema(description = "Data em que o faturamento foi pago ou recebido", example = "2026-06-02T10:00:00", nullable = true)
        LocalDateTime dataPagamento,

        @Schema(description = "Status calculado do faturamento", example = "EM_ABERTO")
        StatusTitulo status,

        @Schema(description = "Observacao opcional do faturamento", example = "Pago via Pix", nullable = true)
        String observacao
) {
}
