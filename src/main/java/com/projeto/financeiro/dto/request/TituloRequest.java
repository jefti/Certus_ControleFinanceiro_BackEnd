package com.projeto.financeiro.dto.request;

import com.projeto.financeiro.entity.enums.Recorrencia;
import com.projeto.financeiro.entity.enums.TipoTitulo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record TituloRequest(
        Long id,

        @Schema(description = "Descricao do titulo", example = "Aluguel")
        @NotBlank
        @Size(max = 255)
        String descricao,

        @Schema(description = "Valor base do titulo", example = "1500.00")
        @NotNull
        @DecimalMin(value = "0.01", message = "valor deve ser maior que zero")
        BigDecimal valor,

        @Schema(description = "Data inicial de vencimento do titulo", example = "2026-06-01")
        @NotNull
        LocalDate dataVencimento,

        @Schema(description = "Tipo da movimentacao financeira", example = "PAGAR")
        @NotNull
        TipoTitulo tipo,

        @Schema(description = "Recorrencia do titulo. Se nulo, o titulo gera apenas uma ocorrencia.", example = "MENSAL", nullable = true)
        Recorrencia recorrencia,

        @Schema(description = "Data final da recorrencia. Obrigatoria quando recorrencia for informada.", example = "2026-12-01", nullable = true)
        LocalDate dataFim,

        @Schema(description = "Lista de ids dos centros de custo associados ao titulo", example = "[1,2]")
        List<Long> centroDeCustoIds
) {
    @AssertTrue(message = "dataFim e obrigatoria quando recorrencia esta definida e deve ser maior ou igual a dataVencimento")
    public boolean isRecorrenciaValida() {
        if (recorrencia == null) return true;
        return dataFim != null && !dataFim.isBefore(dataVencimento);
    }
}