package com.projeto.financeiro.handler;

import com.projeto.financeiro.exception.ApiError;
import com.projeto.financeiro.exception.HttpApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @ExceptionHandler(HttpApiException.class)
    public ResponseEntity<ApiError> handleHttpApiException(
            HttpApiException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                LocalDateTime.now().format(FORMATTER),
                ex.getStatus().value(),
                ex.getTitle(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex, HttpServletRequest request) {
        ex.printStackTrace();
        ApiError error = new ApiError(
                LocalDateTime.now().format(FORMATTER),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno do servidor",
                "Ocorreu um erro inesperado no sistema. Tente novamente mais tarde.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
