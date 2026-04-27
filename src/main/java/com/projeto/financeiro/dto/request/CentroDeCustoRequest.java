package com.projeto.financeiro.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CentroDeCustoRequest(
        Long id,
        @NotBlank @Size(max = 255) String descricao,
        @Size(max = 1000) String observacao
) {
}
