package com.projeto.financeiro.docs;

import com.projeto.financeiro.dto.request.ForgotPasswordRequest;
import com.projeto.financeiro.dto.request.ResetPasswordRequest;
import com.projeto.financeiro.dto.response.SimpleMessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(
        name = "Autenticacao",
        description = "Operacoes de autenticacao e recuperacao de senha"
)
public interface AuthControllerDoc {

    @Operation(
            summary = "Solicitar recuperacao de senha",
            description = "Recebe um email e, se ele estiver cadastrado, envia um codigo de recuperacao"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Solicitacao processada com sucesso",
            content = @Content(schema = @Schema(implementation = SimpleMessageResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Email ausente ou formato invalido",
            content = @Content
    )
    ResponseEntity<SimpleMessageResponse> forgotPassword(@RequestBody ForgotPasswordRequest request);


    @Operation(
            summary = "Redefinir senha",
            description = "Valida o codigo de recuperacao e redefine a senha do usuario"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Senha redefinida com sucesso",
            content = @Content(schema = @Schema(implementation = SimpleMessageResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Codigo invalido ou expirado"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Usuario nao encontrado"
    )
    ResponseEntity<SimpleMessageResponse> resetPassword(@RequestBody ResetPasswordRequest request);
}
