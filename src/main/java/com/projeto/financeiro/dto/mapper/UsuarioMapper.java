package com.projeto.financeiro.dto.mapper;

import com.projeto.financeiro.dto.request.UsuarioRequest;
import com.projeto.financeiro.dto.response.UsuarioResponse;
import com.projeto.financeiro.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioMapper {

    private final PasswordEncoder passwordEncoder;

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
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .celular(request.celular())
                .build();
    }

    public void updateEntity(Usuario entity, UsuarioRequest request) {
        if (entity == null || request == null) {
            return;
        }

        entity.setNome(request.nome());
        entity.setEmail(request.email());
        if (request.senha() != null && !request.senha().isBlank()) {
            entity.setSenha(passwordEncoder.encode(request.senha()));
        }
        entity.setCelular(request.celular());
    }
}
