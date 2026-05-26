package com.projeto.financeiro.controller;

import com.projeto.financeiro.dto.request.TituloRequest;
import com.projeto.financeiro.dto.response.TituloResponse;
import com.projeto.financeiro.service.TituloService;
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
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/titulos")
@Tag(name = "Titulos", description = "Operacoes de cadastro, consulta, atualizacao e inativacao de titulos")
public class TituloController {

    private final TituloService tituloService;

    @PostMapping("/cadastrar")
    @Operation(
            summary = "Cadastrar título",
            description = "Cria um novo título. Se não houver recorrência, o sistema gera um faturamento único. Se houver recorrência e dataFim, o sistema gera as ocorrências correspondentes no cadastro.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "201", description = "Título cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = TituloResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados obrigatorios ausentes ou regra de recorrência inválida", content = @Content)
    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    public ResponseEntity<TituloResponse> cadastrar(@Valid @RequestBody TituloRequest tituloRequest) {
        TituloResponse titulo = tituloService.criar(tituloRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(titulo);
    }

    @GetMapping("/obter")
    @Operation(
            summary = "Listar titulos",
            description = "Retorna todos os titulos do usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Titulos retornados com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TituloResponse.class))))
    @ApiResponse(responseCode = "401", description = "Nao autenticado", content = @Content)
    public ResponseEntity<List<TituloResponse>> obterTodos() {
        return ResponseEntity.ok(tituloService.listarTodos());
    }

    @GetMapping("/obter/{id}")
    @Operation(
            summary = "Buscar titulo por id",
            description = "Retorna os dados de um titulo especifico do usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Titulo encontrado",
            content = @Content(schema = @Schema(implementation = TituloResponse.class)))
    @ApiResponse(responseCode = "401", description = "Nao autenticado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Titulo nao encontrado", content = @Content)
    public ResponseEntity<TituloResponse> obterPorId(@PathVariable long id) {
        return ResponseEntity.ok(tituloService.buscarPorId(id));
    }

    @PutMapping("/atualizar/{id}")
    @Operation(
            summary = "Atualizar titulo",
            description = "Atualiza os dados de um titulo existente. Faturamentos nao pagos podem ser recriados com base na nova regra; faturamentos ja pagos sao preservados.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Titulo atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = TituloResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados obrigatorios ausentes ou regra de recorrencia invalida", content = @Content)
    @ApiResponse(responseCode = "401", description = "Nao autenticado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Titulo nao encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Titulo inativo nao pode ser atualizado", content = @Content)
    public ResponseEntity<TituloResponse> atualizar(@PathVariable long id, @Valid @RequestBody TituloRequest tituloRequest) {
        return ResponseEntity.ok(tituloService.atualizar(id, tituloRequest));
    }

    @DeleteMapping("/deletar/{id}")
    @Operation(
            summary = "Inativar titulo",
            description = "Inativa o titulo sem remover o historico financeiro ja gerado.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "204", description = "Titulo inativado com sucesso", content = @Content)
    @ApiResponse(responseCode = "401", description = "Nao autenticado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Titulo nao encontrado", content = @Content)
    public ResponseEntity<Void> deletar(@PathVariable long id) {
        tituloService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
