package com.projeto.financeiro.dto.mapper;

import com.projeto.financeiro.dto.request.TituloRequest;
import com.projeto.financeiro.dto.response.CentroDeCustoResponse;
import com.projeto.financeiro.dto.response.TituloResponse;
import com.projeto.financeiro.entity.CentroDeCusto;
import com.projeto.financeiro.entity.Titulo;
import com.projeto.financeiro.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TituloMapper {

    private final CentroDeCustoMapper centroDeCustoMapper;

    public TituloResponse toDto(Titulo entity) {
        if (entity == null) {return null;}

        List<CentroDeCustoResponse> centrosDeCusto = entity.getCentroDeCusto() != null
                ? entity.getCentroDeCusto().stream().map(centroDeCustoMapper::toDto).toList()
                : List.of();

        return new TituloResponse(
                entity.getId(),
                entity.getDescricao(),
                entity.getValor(),
                entity.getDataVencimento(),
                entity.getDataPagamento(),
                entity.getTipo(),
                centrosDeCusto
        );
    }

    public Titulo toEntity(TituloRequest request, Usuario usuario, List<CentroDeCusto> centrosDeCusto) {
        if (request == null) {return null;}

        return Titulo.builder()
                .descricao(request.descricao())
                .valor(request.valor())
                .dataVencimento(request.dataVencimento())
                .dataPagamento(request.dataPagamento())
                .tipo(request.tipo())
                .usuario(usuario)
                .centroDeCusto(centrosDeCusto != null ? centrosDeCusto : List.of())
                .build();

    }

    public void updateEntity(Titulo entity, TituloRequest request, List<CentroDeCusto> centrosDeCusto) {
        if (entity == null || request == null) {
            return;
        }

        entity.setDescricao(request.descricao());
        entity.setValor(request.valor());
        entity.setDataVencimento(request.dataVencimento());
        entity.setDataPagamento(request.dataPagamento());
        entity.setTipo(request.tipo());
        entity.setCentroDeCusto(centrosDeCusto != null ? centrosDeCusto : List.of());
    }
}
