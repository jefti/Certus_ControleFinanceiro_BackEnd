package com.projeto.financeiro.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends HttpApiException {
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT, "Conflito de dados");
    }
}
