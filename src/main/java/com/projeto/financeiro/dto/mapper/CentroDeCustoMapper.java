package com.projeto.financeiro.dto.mapper;

import com.projeto.financeiro.dto.request.CentroDeCustoRequest;
import com.projeto.financeiro.dto.response.CentroDeCustoResponse;
import com.projeto.financeiro.entity.CentroDeCusto;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.security.TextSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CentroDeCustoMapper {

    private final TextSanitizer textSanitizer;

    public CentroDeCustoResponse toDto(CentroDeCusto entity) {
        if (entity == null) {
            return null;
        }

        return new CentroDeCustoResponse(
                entity.getId(),
                entity.getDescricao(),
                entity.getObservacao()
        );
    }

    public CentroDeCusto toEntity(CentroDeCustoRequest request, Usuario usuario) {
        if (request == null) {
            return null;
        }

        CentroDeCusto entity = new CentroDeCusto();
        entity.setId(request.id());
        entity.setDescricao(textSanitizer.sanitize(request.descricao()));
        entity.setObservacao(textSanitizer.sanitize(request.observacao()));
        entity.setUsuario(usuario);
        entity.setTitulos(null);
        return entity;
    }

    public void updateEntity(CentroDeCusto entity, CentroDeCustoRequest request) {
        if (entity == null || request == null) {
            return;
        }

        entity.setDescricao(textSanitizer.sanitize(request.descricao()));
        entity.setObservacao(textSanitizer.sanitize(request.observacao()));
    }
}
