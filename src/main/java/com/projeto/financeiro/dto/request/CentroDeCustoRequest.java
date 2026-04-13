package com.projeto.financeiro.dto.request;

public record CentroDeCustoRequest(
        Long id,
        String descricao,
        String observacao
) {
}
