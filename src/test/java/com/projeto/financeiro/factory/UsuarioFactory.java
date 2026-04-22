package com.projeto.financeiro.factory;

import java.time.LocalDateTime;
import java.util.UUID;

import com.projeto.financeiro.dto.request.UsuarioRequest;
import com.projeto.financeiro.entity.Usuario;

public class UsuarioFactory {

    public static UsuarioRequest buildValidUsuarioRequest() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String numericSuffix = String.valueOf(Math.abs(UUID.randomUUID().hashCode()));

        return new UsuarioRequest(
                "user-" + suffix,
                "user-" + suffix + "@email.com",
                "123456",
                numericSuffix.substring(0, Math.min(numericSuffix.length(), 11))
        );
    }

    public static UsuarioRequest buildUsuarioRequest(
            String nome,
            String email,
            String senha,
            String celular
    ) {
        return new UsuarioRequest(nome, email, senha, celular);
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
        usuario.setDataCriacao(LocalDateTime.now());

        return usuario;
    }

    public static Usuario buildUsuario(
            Long id,
            String nome,
            String email,
            String senha,
            String celular,
            LocalDateTime dataCriacao,
            LocalDateTime dataInativacao
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
