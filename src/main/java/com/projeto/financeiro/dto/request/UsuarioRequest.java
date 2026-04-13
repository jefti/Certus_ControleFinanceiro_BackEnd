package com.projeto.financeiro.dto.request;

public record UsuarioRequest(
        String nome,
        String email,
        String senha,
        String celular
) {
}
