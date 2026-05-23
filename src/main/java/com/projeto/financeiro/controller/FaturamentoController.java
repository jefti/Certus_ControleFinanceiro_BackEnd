package com.projeto.financeiro.controller;

import com.projeto.financeiro.dto.request.ValidarFaturamentoRequest;
import com.projeto.financeiro.dto.response.FaturamentoResponse;
import com.projeto.financeiro.service.FaturamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/faturamentos")
@Tag(name = "Faturamentos", description = "Operacoes de consulta e validacao de faturamentos")
public class FaturamentoController {

    private final FaturamentoService faturamentoService;

    @GetMapping("/titulo/{tituloId}")
    @Operation(
            summary = "Listar faturamentos por titulo",
            description = "Retorna todos os faturamentos do titulo informado para o usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Faturamentos retornados com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FaturamentoResponse.class))))
    @ApiResponse(responseCode = "401", description = "Nao autenticado", content = @Content)
    public ResponseEntity<List<FaturamentoResponse>> listarPorTitulo(@PathVariable long tituloId) {
        return ResponseEntity.ok(faturamentoService.listarPorTitulo(tituloId));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar faturamento por id",
            description = "Retorna um faturamento especifico do usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Faturamento encontrado",
            content = @Content(schema = @Schema(implementation = FaturamentoResponse.class)))
    @ApiResponse(responseCode = "401", description = "Nao autenticado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Faturamento nao encontrado", content = @Content)
    public ResponseEntity<FaturamentoResponse> buscarPorId(@PathVariable long id) {
        return ResponseEntity.ok(faturamentoService.buscarPorId(id));
    }

    @PatchMapping("/{id}/validar")
    @Operation(
            summary = "Validar faturamento",
            description = "Marca um faturamento como pago ou recebido",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Faturamento validado com sucesso",
            content = @Content(schema = @Schema(implementation = FaturamentoResponse.class)))
    @ApiResponse(responseCode = "400", description = "Faturamento ja validado", content = @Content)
    @ApiResponse(responseCode = "401", description = "Nao autenticado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Faturamento nao encontrado", content = @Content)
    public ResponseEntity<FaturamentoResponse> validar(
            @PathVariable long id,
            @RequestBody(required = false) ValidarFaturamentoRequest request
    ) {
        return ResponseEntity.ok(faturamentoService.validar(id, request));
    }
}
