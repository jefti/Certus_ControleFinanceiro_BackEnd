package com.projeto.financeiro.docs;

import com.projeto.financeiro.dto.request.UsuarioCreateRequest;
import com.projeto.financeiro.dto.request.UsuarioUpdateRequest;
import com.projeto.financeiro.dto.response.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(
        name = "Usuarios",
        description = "Operacoes de cadastro, consulta, atualizacao e inativacao de usuarios"
)
public interface UsuarioControllerDoc {

    @Operation(
            summary = "Cadastrar usuario",
            description = "Cria um novo usuario na base de dados"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Usuario cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Dados obrigatorios ausentes"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Email ou celular ja cadastrado"
    )
    ResponseEntity<UsuarioResponse> cadastrar(@RequestBody UsuarioCreateRequest usuarioRequest);

    @Operation(
            summary = "Buscar meu perfil",
            description = "Retorna os dados do usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Perfil retornado com sucesso",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Nao autenticado"
    )
    ResponseEntity<UsuarioResponse> obterMeuPerfil();

    @Operation(
            summary = "Atualizar usuario",
            description = "Atualiza os dados de um usuario existente",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Usuario atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Dados obrigatorios ausentes"
    )
    @ApiResponse(
            responseCode = "401",
            description = "Nao autenticado"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Usuario nao encontrado"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Email ou celular ja cadastrado para outro usuario"
    )
    ResponseEntity<UsuarioResponse> atualizar(@RequestBody UsuarioUpdateRequest usuarioRequest);

    @Operation(
            summary = "Inativar usuario",
            description = "Realiza a inativacao logica de um usuario pelo id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "204",
            description = "Usuario inativado com sucesso"
    )
    @ApiResponse(
            responseCode = "401",
            description = "Nao autenticado"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Usuario nao encontrado"
    )
    ResponseEntity<Void> deletar();
}
