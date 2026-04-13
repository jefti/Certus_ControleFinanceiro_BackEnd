package com.projeto.financeiro.dto.response;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String celular,
        LocalDateTime dataCadastro,
        LocalDateTime dataInativacao
) {
}
