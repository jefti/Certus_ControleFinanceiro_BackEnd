package com.projeto.financeiro.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projeto.financeiro.dto.request.ForgotPasswordRequest;
import com.projeto.financeiro.dto.request.ResetPasswordRequest;
import com.projeto.financeiro.dto.response.SimpleMessageResponse;
import com.projeto.financeiro.service.RecuperacaoSenhaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacao", description = "Operacoes de autenticacao e recuperacao de senha")
public class AuthController {

    private final RecuperacaoSenhaService recuperacaoSenhaService;

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Solicitar recuperacao de senha",
            description = "Recebe um email e, se ele estiver cadastrado, envia um codigo de recuperacao."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Solicitacao processada com sucesso",
                    content = @Content(schema = @Schema(implementation = SimpleMessageResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Usuario nao encontrado", content = @Content),
            @ApiResponse(responseCode = "503", description = "Falha ao enviar email de recuperacao", content = @Content)
    })
    public ResponseEntity<SimpleMessageResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(recuperacaoSenhaService.solicitarRecuperacao(request));
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Redefinir senha",
            description = "Valida o codigo de recuperacao e redefine a senha do usuario."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Senha redefinida com sucesso",
                    content = @Content(schema = @Schema(implementation = SimpleMessageResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Codigo invalido ou expirado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario nao encontrado", content = @Content),
            @ApiResponse(responseCode = "503", description = "Falha no servico de email", content = @Content)
    })
    public ResponseEntity<SimpleMessageResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(recuperacaoSenhaService.resetarSenha(request));
    }
}
