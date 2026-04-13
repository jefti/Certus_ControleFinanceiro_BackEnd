package com.projeto.financeiro.dto.mapper;

import com.projeto.financeiro.dto.request.CentroDeCustoRequest;
import com.projeto.financeiro.dto.response.CentroDeCustoResponse;
import com.projeto.financeiro.entity.CentroDeCusto;
import com.projeto.financeiro.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class CentroDeCustoMapper {

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
        entity.setDescricao(request.descricao());
        entity.setObservacao(request.observacao());
        entity.setUsuario(usuario);
        entity.setTitulos(null);
        return entity;
    }

    public void updateEntity(CentroDeCusto entity, CentroDeCustoRequest request) {
        if (entity == null || request == null) {
            return;
        }

        entity.setDescricao(request.descricao());
        entity.setObservacao(request.observacao());
    }
}
