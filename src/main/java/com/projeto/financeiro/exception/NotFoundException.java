package com.projeto.financeiro.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends HttpApiException {
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "Recurso não encontrado");
    }
}
