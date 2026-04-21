package com.projeto.financeiro.dto.response;

import java.time.Instant;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String celular,
        Instant dataCadastro,
        Instant dataInativacao
) {
}
