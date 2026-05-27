package com.projeto.financeiro.handler;

import com.projeto.financeiro.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private HttpServletRequest mockRequest(String uri) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(uri);
        return request;
    }

    @Test
    void shouldReturnServiceUnavailableWhenEmailDeliveryFails() {
        HttpServletRequest request = mockRequest("/auth/forgot-password");

        ResponseEntity<ApiError> response = handler.handleEmailDeliveryException(
                new EmailDeliveryException("Falha ao enviar email de recuperacao."), request);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(503, response.getBody().status());
        assertEquals("Falha no envio de email", response.getBody().erro());
        assertEquals("Falha ao enviar email de recuperacao.", response.getBody().mensagem());
        assertEquals("/auth/forgot-password", response.getBody().path());
    }

    @Test
    void shouldReturnNotFoundWhenResourceDoesNotExist() {
        HttpServletRequest request = mockRequest("/api/titulos/99");

        ResponseEntity<ApiError> response = handler.handleNotFound(
                new NotFoundException("Título não encontrado com id: 99"), request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("Recurso não encontrado", response.getBody().erro());
        assertEquals("Título não encontrado com id: 99", response.getBody().mensagem());
        assertEquals("/api/titulos/99", response.getBody().path());
    }

    @Test
    void shouldReturnBadRequestOnBusinessError() {
        HttpServletRequest request = mockRequest("/api/titulos");

        ResponseEntity<ApiError> response = handler.handleBadRequestException(
                new BadRequestException("Campos obrigatórios ausentes: descricao"), request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Erro de negócio", response.getBody().erro());
    }

    @Test
    void shouldReturnConflictOnDuplicateData() {
        HttpServletRequest request = mockRequest("/api/usuarios");

        ResponseEntity<ApiError> response = handler.handleConflict(
                new ConflictException("Usuário com email john@email.com já existe."), request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().status());
        assertEquals("Conflito de dados", response.getBody().erro());
        assertEquals("Usuário com email john@email.com já existe.", response.getBody().mensagem());
    }

    @Test
    void shouldReturnInternalServerErrorOnUnexpectedException() {
        HttpServletRequest request = mockRequest("/api/algo");

        ResponseEntity<ApiError> response = handler.handleGenericException(
                new RuntimeException("Erro inesperado"), request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertEquals("Erro interno do servidor", response.getBody().erro());
    }

    @Test
    void shouldReturnBadRequestOnValidationError() {
        HttpServletRequest request = mockRequest("/api/titulos");

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("tituloRequest", "descricao", "must not be blank");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ApiError> response = handler.handleValidationException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Erro de validacao", response.getBody().erro());
        assertTrue(response.getBody().mensagem().contains("descricao"));
    }
}