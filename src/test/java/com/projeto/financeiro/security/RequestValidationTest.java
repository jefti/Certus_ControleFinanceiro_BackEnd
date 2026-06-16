package com.projeto.financeiro.security;

import com.projeto.financeiro.dto.request.ResetPasswordRequest;
import com.projeto.financeiro.dto.request.UsuarioCreateRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class RequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldRejectInvalidUserForm() {
        UsuarioCreateRequest request = new UsuarioCreateRequest("", "email-invalido", "123", "telefone");

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectInvalidPasswordResetForm() {
        ResetPasswordRequest request = new ResetPasswordRequest("email-invalido", "12", "123");

        assertFalse(validator.validate(request).isEmpty());
    }
}
