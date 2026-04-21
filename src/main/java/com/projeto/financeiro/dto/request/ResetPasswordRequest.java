package com.projeto.financeiro.dto.request;

public record ResetPasswordRequest (
    String email,
    String codigo,
    String novaSenha
){

}
