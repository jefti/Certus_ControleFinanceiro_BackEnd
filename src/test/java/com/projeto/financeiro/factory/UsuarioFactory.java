package com.projeto.financeiro.factory;

import java.time.Instant;
import java.util.UUID;

import com.projeto.financeiro.dto.request.UsuarioCreateRequest;
import com.projeto.financeiro.dto.request.UsuarioUpdateRequest;
import com.projeto.financeiro.entity.Usuario;

public class UsuarioFactory {

    public static UsuarioCreateRequest buildValidUsuarioCreateRequest() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String numericSuffix = String.valueOf(Math.abs(UUID.randomUUID().hashCode()));

        return new UsuarioCreateRequest(
                "user-" + suffix,
                "user-" + suffix + "@email.com",
                "12345678",
                numericSuffix.substring(0, Math.min(numericSuffix.length(), 11))
        );
    }

    public static UsuarioCreateRequest buildUsuarioCreateRequest(
            String nome,
            String email,
            String senha,
            String celular
    ) {
        return new UsuarioCreateRequest(nome, email, senha, celular);
    }

    public static UsuarioUpdateRequest buildUsuarioUpdateRequest(
            String nome,
            String email,
            String senha,
            String celular
    ) {
        return new UsuarioUpdateRequest(nome, email, senha, celular);
    }

    public static Usuario buildValidUsuario() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String numericSuffix = String.valueOf(Math.abs(UUID.randomUUID().hashCode()));

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("user-" + suffix);
        usuario.setEmail("user-" + suffix + "@email.com");
        usuario.setSenha("123456");
        usuario.setCelular(numericSuffix.substring(0, Math.min(numericSuffix.length(), 11)));
        usuario.setDataCriacao(Instant.now());

        return usuario;
    }

    public static Usuario buildUsuario(
            Long id,
            String nome,
            String email,
            String senha,
            String celular,
            Instant dataCriacao,
            Instant dataInativacao
    ) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setCelular(celular);
        usuario.setDataCriacao(dataCriacao);
        usuario.setDataInativacao(dataInativacao);

        return usuario;
    }
}
