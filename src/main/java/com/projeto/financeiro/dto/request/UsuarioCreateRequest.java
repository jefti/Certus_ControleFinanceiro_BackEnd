package com.projeto.financeiro.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioCreateRequest(
        @NotBlank @Size(max = 255) String nome,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8, max = 72) String senha,
        @NotBlank @Pattern(regexp = "\\d{10,15}", message = "deve conter entre 10 e 15 digitos") String celular
) {
    public String telefone() {
        return celular;
    }
}