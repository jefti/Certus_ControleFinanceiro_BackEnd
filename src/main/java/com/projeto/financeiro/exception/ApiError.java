package com.projeto.financeiro.exception;

public record ApiError(
        String timestamp,
        int status,
        String erro,
        String mensagem,
        String path
) {}
