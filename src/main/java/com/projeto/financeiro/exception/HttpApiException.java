package com.projeto.financeiro.exception;

import org.springframework.http.HttpStatus;


public abstract class HttpApiException extends RuntimeException {

    private final HttpStatus status;
    private final String title;

    protected HttpApiException(String message, HttpStatus status, String title) {
        super(message);
        this.status = status;
        this.title = title;
    }

    protected HttpApiException(String message, HttpStatus status, String title, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.title = title;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }
}
