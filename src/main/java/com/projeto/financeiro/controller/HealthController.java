package com.projeto.financeiro.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.financeiro.dto.response.HealthResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@Tag(name = "Health", description = "Verificação de integridade da aplicação")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Verifica se a aplicação está ativa", description = "Retorna o status básico de funcionamento da API")
    @ApiResponse(responseCode = "200", description = "Aplicação ativa", content = @Content(schema = @Schema(implementation = HealthResponse.class)))
    public ResponseEntity<HealthResponse> health(){
        HealthResponse response = new HealthResponse(
                "UP",
                "financeiro",
                OffsetDateTime.now()
        );
        return ResponseEntity.ok(response);
    }
}
