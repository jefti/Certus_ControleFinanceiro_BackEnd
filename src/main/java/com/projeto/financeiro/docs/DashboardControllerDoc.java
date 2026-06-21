package com.projeto.financeiro.docs;

import com.projeto.financeiro.dto.response.DashboardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(
        name = "Dashboard",
        description = "Indicadores financeiros consolidados do usuario"
)
public interface DashboardControllerDoc {

    @Operation(
            summary = "Obter dashboard financeira",
            description = "Retorna indicadores consolidados: receitas, despesas, saldo, atrasados, proximos vencimentos e distribuicao por centro de custo",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Dashboard gerada com sucesso",
            content = @Content(schema = @Schema(implementation = DashboardResponse.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Nao autenticado"
    )
    ResponseEntity<DashboardResponse> obter(
            @RequestParam LocalDate periodoInicial,
            @RequestParam LocalDate periodoFinal
    );

    @Operation(
            summary = "Exportar dashboard em Excel",
            description = "Gera um arquivo .xlsx com 4 abas: Resumo, Fluxo de Caixa, Centros de Custo (com títulos agrupados) e Lançamentos detalhados. Valores positivos em verde, negativos em vermelho.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Arquivo Excel gerado com sucesso",
            content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    )
    @ApiResponse(responseCode = "401", description = "Nao autenticado")
    @GetMapping("/export")
    ResponseEntity<byte[]> exportar(
            @RequestParam LocalDate periodoInicial,
            @RequestParam LocalDate periodoFinal
    );
}
