package com.projeto.financeiro.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.projeto.financeiro.exception.ApiError;
import com.projeto.financeiro.exception.EmailDeliveryException;

import jakarta.servlet.http.HttpServletRequest;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturnServiceUnavailableWhenEmailDeliveryFails() {
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/auth/forgot-password");

        ResponseEntity<ApiError> response = handler.handleEmailDeliveryException(
                new EmailDeliveryException("Falha ao enviar email de recuperacao."),
                request);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(503, response.getBody().status());
        assertEquals("Falha no envio de email", response.getBody().erro());
        assertEquals("Falha ao enviar email de recuperacao.", response.getBody().mensagem());
        assertEquals("/auth/forgot-password", response.getBody().path());
    }
}
