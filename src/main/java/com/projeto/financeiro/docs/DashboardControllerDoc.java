package com.projeto.financeiro.docs;

import com.projeto.financeiro.dto.response.DashboardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Tag(
        name = "Dashboard",
        description = "Operacoes de consulta do fluxo de caixa consolidado por periodo"
)
public interface DashboardControllerDoc {

    @Operation(
            summary = "Consultar dashboard por periodo",
            description = "Retorna o resumo financeiro, a serie de fluxo de caixa e os lancamentos do periodo informado para o usuario autenticado",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(
            responseCode = "200",
            description = "Dashboard retornado com sucesso",
            content = @Content(schema = @Schema(implementation = DashboardResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Periodo informado invalido"
    )
    @ApiResponse(
            responseCode = "401",
            description = "Nao autenticado"
    )
    ResponseEntity<DashboardResponse> obterDashboard(
            @RequestParam LocalDate periodoInicial,
            @RequestParam LocalDate periodoFinal
    );
}
