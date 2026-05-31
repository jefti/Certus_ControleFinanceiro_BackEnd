package com.projeto.financeiro.docs;

import com.projeto.financeiro.dto.request.CentroDeCustoRequest;
import com.projeto.financeiro.dto.response.CentroDeCustoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(
        name = "Centros de Custo",
        description = "Operações de cadastro, consulta, atualização e inativação de centros de custo"
)
public interface CentroDeCustoControllerDoc {

    @Operation(
            summary = "Cadastrar centro de custo",
            description = "Cria um novo centro de custo vinculado ao usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "201", description = "Centro de custo cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = CentroDeCustoResponse.class))
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
            responseCode = "409",
            description = "Centro de custo com a mesma descricao ja cadastrado"
    )
    ResponseEntity<CentroDeCustoResponse> cadastrar(@RequestBody CentroDeCustoRequest centroDeCustoRequest);


    @Operation(
            summary = "Listar centros de custo",
            description = "Retorna todos os centros de custo do usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Centros de custo retornados com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CentroDeCustoResponse.class)))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Nao autenticado"
    )
    ResponseEntity<List<CentroDeCustoResponse>> obterTodos();

    @Operation(
            summary = "Buscar centro de custo por id",
            description = "Retorna os dados de um centro de custo especifico do usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Centro de custo encontrado",
            content = @Content(schema = @Schema(implementation = CentroDeCustoResponse.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Nao autenticado"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Centro de custo nao encontrado"
    )
    ResponseEntity<CentroDeCustoResponse> obterPorId(@PathVariable long id);


    @Operation(
            summary = "Atualizar centro de custo",
            description = "Atualiza os dados de um centro de custo existente do usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Centro de custo atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = CentroDeCustoResponse.class))
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
            description = "Centro de custo nao encontrado"
    )
    ResponseEntity<CentroDeCustoResponse> atualizar(@PathVariable long id, @RequestBody CentroDeCustoRequest centroDeCustoRequest);

    @Operation(
            summary = "Inativar centro de custo",
            description = "Realiza a remocao de um centro de custo pelo id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "204",
            description = "Centro de custo inativado com sucesso"
    )
    @ApiResponse(
            responseCode = "401",
            description = "Nao autenticado"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Centro de custo nao encontrado"
    )
    ResponseEntity<Void> deletar(@PathVariable long id);

}
