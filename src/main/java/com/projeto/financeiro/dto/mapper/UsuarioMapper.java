package com.projeto.financeiro.dto.mapper;

import com.projeto.financeiro.dto.request.UsuarioRequest;
import com.projeto.financeiro.dto.response.UsuarioResponse;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.security.TextSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioMapper {

    private final PasswordEncoder passwordEncoder;
    private final TextSanitizer textSanitizer;

    public UsuarioResponse toDto(Usuario entity) {
        if (entity == null) {
            return null;
        }

        return new UsuarioResponse(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getCelular(),
                entity.getDataCriacao(),
                entity.getDataInativacao()
        );
    }

    public Usuario toEntity(UsuarioRequest request) {
        if (request == null) {
            return null;
        }

        return Usuario.builder()
                .nome(textSanitizer.sanitize(request.nome()))
                .email(request.email().trim().toLowerCase())
                .senha(passwordEncoder.encode(request.senha()))
                .celular(request.celular())
                .build();
    }

    public void updateEntity(Usuario entity, UsuarioRequest request) {
        if (entity == null || request == null) {
            return;
        }

        entity.setNome(textSanitizer.sanitize(request.nome()));
        entity.setEmail(request.email().trim().toLowerCase());
        if (request.senha() != null && !request.senha().isBlank()) {
            entity.setSenha(passwordEncoder.encode(request.senha()));
        }
        entity.setCelular(request.celular());
    }
}
