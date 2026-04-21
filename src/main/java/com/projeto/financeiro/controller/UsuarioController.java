package com.projeto.financeiro.controller;

import com.projeto.financeiro.dto.request.UsuarioRequest;
import com.projeto.financeiro.dto.response.UsuarioResponse;
import com.projeto.financeiro.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Operacoes de cadastro, consulta, atualizacao e inativacao de usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    @PostMapping("/cadastrar")
    @Operation(summary = "Cadastrar usuario", description = "Cria um novo usuario na base de dados")
    @ApiResponse(responseCode = "201", description = "Usuario cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados obrigatorios ausentes", content = @Content)
    @ApiResponse(responseCode = "409", description = "Email ou celular ja cadastrado", content = @Content)
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody UsuarioRequest usuarioRequest) {
        UsuarioResponse usuario = usuarioService.criar(usuarioRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuario);
    }

    @GetMapping("/obter")
    @Operation(
            summary = "Listar usuarios",
            description = "Retorna todos os usuarios cadastrados",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Usuarios retornados com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UsuarioResponse.class))))
    @ApiResponse(responseCode = "401", description = "Nao autenticado", content = @Content)
    public ResponseEntity<List<UsuarioResponse>> obterTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/obter/{id}")
    @Operation(
            summary = "Buscar usuario por id",
            description = "Retorna os dados de um usuario especifico",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    @ApiResponse(responseCode = "401", description = "Nao autenticado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Usuario nao encontrado", content = @Content)
    public ResponseEntity<UsuarioResponse> obterPorId(@PathVariable long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PutMapping("/atualizar/{id}")
    @Operation(
            summary = "Atualizar usuario",
            description = "Atualiza os dados de um usuario existente",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Usuario atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados obrigatorios ausentes", content = @Content)
    @ApiResponse(responseCode = "401", description = "Nao autenticado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Usuario nao encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Email ou celular ja cadastrado para outro usuario", content = @Content)
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable long id, @RequestBody UsuarioRequest usuarioRequest) {
        return ResponseEntity.ok(usuarioService.atualizar(id, usuarioRequest));
    }

    @DeleteMapping("/deletar/{id}")
    @Operation(
            summary = "Inativar usuario",
            description = "Realiza a inativacao logica de um usuario pelo id",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "204", description = "Usuario inativado com sucesso", content = @Content)
    @ApiResponse(responseCode = "401", description = "Nao autenticado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Usuario nao encontrado", content = @Content)
    public ResponseEntity<Void> deletar(@PathVariable long id) {
        usuarioService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
