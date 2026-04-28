package com.projeto.financeiro.dto.request;

import com.projeto.financeiro.entity.enums.TipoTitulo;
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
        List<Long> centroDeCustoIds
) {
}
