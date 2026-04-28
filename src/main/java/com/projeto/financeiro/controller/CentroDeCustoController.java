package com.projeto.financeiro.controller;

import com.projeto.financeiro.dto.request.CentroDeCustoRequest;
import com.projeto.financeiro.dto.response.CentroDeCustoResponse;
import com.projeto.financeiro.service.CentroDeCustoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/centros-de-custo")
@Tag(name = "Centros de Custo", description = "Operacoes de cadastro, consulta, atualizacao e inativacao de centros de custo")
public class CentroDeCustoController {

    private final CentroDeCustoService centroDeCustoService;

    @PostMapping("/cadastrar")
    @Operation(
            summary = "Cadastrar centro de custo",
            description = "Cria um novo centro de custo vinculado ao usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "201", description = "Centro de custo cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = CentroDeCustoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados obrigatorios ausentes", content = @Content)
    @ApiResponse(responseCode = "401", description = "Nao autenticado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Centro de custo com a mesma descricao ja cadastrado", content = @Content)
    public ResponseEntity<CentroDeCustoResponse> cadastrar(@RequestBody CentroDeCustoRequest centroDeCustoRequest) {
        CentroDeCustoResponse centroDeCusto = centroDeCustoService.criar(centroDeCustoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(centroDeCusto);
    }

    @GetMapping("/obter")
    @Operation(
            summary = "Listar centros de custo",
            description = "Retorna todos os centros de custo do usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Centros de custo retornados com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CentroDeCustoResponse.class))))
    @ApiResponse(responseCode = "401", description = "Nao autenticado", content = @Content)
    public ResponseEntity<List<CentroDeCustoResponse>> obterTodos() {
        return ResponseEntity.ok(centroDeCustoService.listarTodos());
    }

    @GetMapping("/obter/{id}")
    @Operation(
            summary = "Buscar centro de custo por id",
            description = "Retorna os dados de um centro de custo especifico do usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Centro de custo encontrado",
            content = @Content(schema = @Schema(implementation = CentroDeCustoResponse.class)))
    @ApiResponse(responseCode = "401", description = "Nao autenticado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Centro de custo nao encontrado", content = @Content)
    public ResponseEntity<CentroDeCustoResponse> obterPorId(@PathVariable long id) {
        return ResponseEntity.ok(centroDeCustoService.buscarPorId(id));
    }

    @PutMapping("/atualizar/{id}")
    @Operation(
            summary = "Atualizar centro de custo",
            description = "Atualiza os dados de um centro de custo existente do usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Centro de custo atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = CentroDeCustoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados obrigatorios ausentes", content = @Content)
    @ApiResponse(responseCode = "401", description = "Nao autenticado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Centro de custo nao encontrado", content = @Content)
    public ResponseEntity<CentroDeCustoResponse> atualizar(@PathVariable long id, @RequestBody CentroDeCustoRequest centroDeCustoRequest) {
        return ResponseEntity.ok(centroDeCustoService.atualizar(id, centroDeCustoRequest));
    }

    @DeleteMapping("/deletar/{id}")
    @Operation(
            summary = "Inativar centro de custo",
            description = "Realiza a remocao de um centro de custo pelo id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "204", description = "Centro de custo inativado com sucesso", content = @Content)
    @ApiResponse(responseCode = "401", description = "Nao autenticado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Centro de custo nao encontrado", content = @Content)
    public ResponseEntity<Void> deletar(@PathVariable long id) {
        centroDeCustoService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
