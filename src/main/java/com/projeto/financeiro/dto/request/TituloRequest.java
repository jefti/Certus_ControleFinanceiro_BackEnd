package com.projeto.financeiro.dto.request;

import com.projeto.financeiro.entity.enums.Recorrencia;
import com.projeto.financeiro.entity.enums.TipoTitulo;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record TituloRequest(
        Long id,
        @NotBlank @Size(max = 255) String descricao,
        @NotNull @DecimalMin(value = "0.01", message = "valor deve ser maior que zero") BigDecimal valor,
        @NotNull LocalDate dataVencimento,
        LocalDateTime dataPagamento,
        @NotNull TipoTitulo tipo,
        Recorrencia recorrencia,
        LocalDate dataFim,
        List<Long> centroDeCustoIds
) {
    @AssertTrue(message = "dataFim é obrigatória quando recorrencia está definida e deve ser >= dataVencimento")
    public boolean isRecorrenciaValida() {
    if (recorrencia == null) return true;
    return dataFim != null && !dataFim.isBefore(dataVencimento);
}
}
