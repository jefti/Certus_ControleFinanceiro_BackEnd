package com.projeto.financeiro.dto.request;

import java.time.LocalDateTime;

public record ValidarFaturamentoRequest(
        LocalDateTime dataPagamento,
        String observacao
) {
}