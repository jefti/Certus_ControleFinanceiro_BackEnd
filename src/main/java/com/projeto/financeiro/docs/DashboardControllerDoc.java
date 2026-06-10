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
}
