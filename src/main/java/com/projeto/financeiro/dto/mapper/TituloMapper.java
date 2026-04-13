package com.projeto.financeiro.dto.mapper;

import com.projeto.financeiro.dto.request.TituloRequest;
import com.projeto.financeiro.dto.response.TituloResponse;
import com.projeto.financeiro.entity.Titulo;
import com.projeto.financeiro.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class TituloMapper {

    public TituloResponse toDto(Titulo entity) {
        if (entity == null) {return null;}

        // TODO: quando TituloResponse tiver List<CentroDeCustoResponse>, mapear entity.getCentroDeCusto() aqui.
        return new TituloResponse(
                entity.getId(),
                entity.getDescricao(),
                entity.getValor(),
                entity.getDataVencimento(),
                entity.getDataPagamento(),
                entity.getTipo()
        );
    }

    public Titulo toEntity(TituloRequest request, Usuario usuario) {
        if (request == null) {return null;}

        // TODO: quando TituloRequest tiver List<CentroDeCustoRequest>, mapear para entity.setCentroDeCusto(...).
        return Titulo.builder()
                .descricao(request.descricao())
                .valor(request.valor())
                .dataVencimento(request.dataVencimento())
                .dataPagamento(request.dataPagamento())
                .tipo(request.tipo())
                .usuario(usuario)
                .build();

    }

    public void updateEntity(Titulo entity, TituloRequest request) {
        if (entity == null || request == null) {
            return;
        }

        entity.setDescricao(request.descricao());
        entity.setValor(request.valor());
        entity.setDataVencimento(request.dataVencimento());
        entity.setDataPagamento(request.dataPagamento());
        entity.setTipo(request.tipo());
    }
}