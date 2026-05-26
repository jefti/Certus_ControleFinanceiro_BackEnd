package com.projeto.financeiro.dto.response;

import com.projeto.financeiro.entity.enums.Recorrencia;
import com.projeto.financeiro.entity.enums.TipoTitulo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record TituloResponse(
        @Schema(description = "Id do titulo", example = "10")
        Long id,

        @Schema(description = "Descricao do titulo", example = "Aluguel")
        String descricao,

        @Schema(description = "Valor base do titulo", example = "1500.00")
        BigDecimal valor,

        @Schema(description = "Data inicial de vencimento", example = "2026-06-01")
        LocalDate dataVencimento,

        @Schema(description = "Tipo da movimentacao", example = "PAGAR")
        TipoTitulo tipo,

        @Schema(description = "Recorrencia configurada no titulo", example = "MENSAL", nullable = true)
        Recorrencia recorrencia,

        @Schema(description = "Data final da recorrencia", example = "2026-12-01", nullable = true)
        LocalDate dataFim,

        @Schema(description = "Indica se o titulo esta ativo", example = "true")
        boolean ativo,

        @Schema(description = "Quantidade de faturamentos atualmente associados ao titulo", example = "6")
        int quantidadeFaturamentos,

        @Schema(description = "Centros de custo vinculados ao titulo")
        List<CentroDeCustoResponse> centrosDeCusto
) {
}