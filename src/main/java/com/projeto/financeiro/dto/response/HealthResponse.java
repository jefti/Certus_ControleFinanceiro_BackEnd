package com.projeto.financeiro.dto.response;

import java.time.Instant;
import java.time.OffsetDateTime;

public record HealthResponse(
        String status,
        String service,
        Instant timestamp
) {
}