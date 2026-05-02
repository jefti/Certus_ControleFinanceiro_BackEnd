package com.projeto.financeiro.exception;

import org.springframework.http.HttpStatus;

public class EmailDeliveryException extends HttpApiException {

    public EmailDeliveryException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE, "Falha no envio de email");
    }

    public EmailDeliveryException(String message, Throwable cause) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE, "Falha no envio de email", cause);
    }
}
