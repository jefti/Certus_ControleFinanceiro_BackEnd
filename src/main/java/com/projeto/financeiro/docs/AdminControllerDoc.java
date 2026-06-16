package com.projeto.financeiro.docs;

import com.projeto.financeiro.dto.response.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(
        name = "Admin",
        description = "Operacoes administrativas de gestao de contas (papel ADMIN). Nao acessa dados financeiros."
)
public interface AdminControllerDoc {

    @Operation(
            summary = "Listar usuarios",
            description = "Retorna o fluxo de contas cadastradas (apenas ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Usuarios retornados com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UsuarioResponse.class)))
    )
    @ApiResponse(responseCode = "401", description = "Nao autenticado")
    @ApiResponse(responseCode = "403", description = "Sem permissao de ADMIN")
    ResponseEntity<List<UsuarioResponse>> listarUsuarios();

    @Operation(
            summary = "Buscar usuario por id",
            description = "Retorna os dados de uma conta especifica (apenas ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Usuario encontrado",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))
    )
    @ApiResponse(responseCode = "401", description = "Nao autenticado")
    @ApiResponse(responseCode = "403", description = "Sem permissao de ADMIN")
    @ApiResponse(responseCode = "404", description = "Usuario nao encontrado")
    ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable long id);

    @Operation(
            summary = "Desativar conta",
            description = "Inativa logicamente a conta de um usuario (apenas ADMIN). Contas ADMIN nao podem ser desativadas.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "204", description = "Conta desativada com sucesso")
    @ApiResponse(responseCode = "400", description = "Conta ADMIN nao pode ser desativada")
    @ApiResponse(responseCode = "401", description = "Nao autenticado")
    @ApiResponse(responseCode = "403", description = "Sem permissao de ADMIN")
    @ApiResponse(responseCode = "404", description = "Usuario nao encontrado")
    ResponseEntity<Void> desativar(@PathVariable long id);

    @Operation(
            summary = "Reativar conta",
            description = "Reativa uma conta previamente inativada (apenas ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "204", description = "Conta reativada com sucesso")
    @ApiResponse(responseCode = "401", description = "Nao autenticado")
    @ApiResponse(responseCode = "403", description = "Sem permissao de ADMIN")
    @ApiResponse(responseCode = "404", description = "Usuario nao encontrado")
    ResponseEntity<Void> reativar(@PathVariable long id);
}
