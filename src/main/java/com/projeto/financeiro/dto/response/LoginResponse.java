package com.projeto.financeiro.dto.response;

public record LoginResponse (
        String token,
        UsuarioResponse usuario
) {
}

