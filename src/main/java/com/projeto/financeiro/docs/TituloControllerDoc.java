package com.projeto.financeiro.docs;

import com.projeto.financeiro.dto.request.TituloRequest;
import com.projeto.financeiro.dto.response.TituloResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(
        name = "Titulos",
        description = "Operacoes de cadastro, consulta, atualizacao e inativacao de titulos"
)
public interface TituloControllerDoc {

    @Operation(
            summary = "Cadastrar título",
            description = "Cria um novo título. Se não houver recorrência, o sistema gera um faturamento único. Se houver recorrência e dataFim, o sistema gera as ocorrências correspondentes no cadastro.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "201",
            description = "Título cadastrado com sucesso",
            content = @Content(schema = @Schema(implementation = TituloResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Dados obrigatorios ausentes ou regra de recorrência inválida"
    )
    @ApiResponse(
            responseCode = "401",
            description = "Não autenticado"
    )
    ResponseEntity<TituloResponse> cadastrar(@Valid @RequestBody TituloRequest tituloRequest);

    @Operation(
            summary = "Listar titulos",
            description = "Retorna todos os titulos do usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Titulos retornados com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TituloResponse.class)))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Nao autenticado"
    )
    ResponseEntity<List<TituloResponse>> obterTodos();

    @Operation(
            summary = "Buscar titulo por id",
            description = "Retorna os dados de um titulo especifico do usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Titulo encontrado",
            content = @Content(schema = @Schema(implementation = TituloResponse.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Nao autenticado"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Titulo nao encontrado"
    )
    ResponseEntity<TituloResponse> obterPorId(@PathVariable long id);

    @Operation(
            summary = "Atualizar titulo",
            description = "Atualiza os dados de um titulo existente. Faturamentos nao pagos podem ser recriados com base na nova regra; faturamentos ja pagos sao preservados.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Titulo atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = TituloResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Dados obrigatorios ausentes ou regra de recorrencia invalida"
    )
    @ApiResponse(
            responseCode = "401",
            description = "Nao autenticado"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Titulo nao encontrado"
    )
    @ApiResponse(
            responseCode = "409",
            description = "Titulo inativo nao pode ser atualizado"
    )
    ResponseEntity<TituloResponse> atualizar(@PathVariable long id, @Valid @RequestBody TituloRequest tituloRequest);

    @Operation(
            summary = "Inativar titulo",
            description = "Inativa o titulo sem remover o historico financeiro ja gerado.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "204",
            description = "Titulo inativado com sucesso"
    )
    @ApiResponse(
            responseCode = "401",
            description = "Nao autenticado"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Titulo nao encontrado"
    )
    ResponseEntity<Void> deletar(@PathVariable long id);
}
