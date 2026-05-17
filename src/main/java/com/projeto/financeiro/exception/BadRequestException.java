package com.projeto.financeiro.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends HttpApiException {
    public BadRequestException(String mensagem) {
        super(mensagem, HttpStatus.BAD_REQUEST, "Erro de negócio");
    }
}
