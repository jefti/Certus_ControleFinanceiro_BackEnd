package com.projeto.financeiro.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Health", description = "Verificacao basica da aplicacao")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Verifica se a aplicacao esta ativa")
    @ApiResponse(responseCode = "200", description = "Aplicacao ativa", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("UP");
    }
}
