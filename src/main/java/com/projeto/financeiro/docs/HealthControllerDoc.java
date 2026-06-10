package com.projeto.financeiro.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
        name = "Health",
        description = "Verificação de saúde e disponibilidade da aplicação"
)
public interface HealthControllerDoc {

    @Operation(
            summary = "Verifica se a aplicacao esta ativa",
            description = "Endpoint público de health check para monitoramento e orquestradores (Kubernetes, Docker, etc.). Não requer autenticação."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Aplicacao ativa e funcionando",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class, example = "UP")
            )
    )
    ResponseEntity<String> health();
}
