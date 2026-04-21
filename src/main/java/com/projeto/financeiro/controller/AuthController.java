package com.projeto.financeiro.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projeto.financeiro.dto.request.ForgotPasswordRequest;
import com.projeto.financeiro.dto.request.ResetPasswordRequest;
import com.projeto.financeiro.dto.response.SimpleMessageResponse;
import com.projeto.financeiro.service.RecuperacaoSenhaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RecuperacaoSenhaService recuperacaoSenhaService;

    @PostMapping("/forgot-password")
    public ResponseEntity<SimpleMessageResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(recuperacaoSenhaService.solicitarRecuperacao(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<SimpleMessageResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(recuperacaoSenhaService.resetarSenha(request));
    }
}
