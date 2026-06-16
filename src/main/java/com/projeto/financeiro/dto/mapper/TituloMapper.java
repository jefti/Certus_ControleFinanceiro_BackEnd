package com.projeto.financeiro.dto.mapper;

import com.projeto.financeiro.dto.request.TituloRequest;
import com.projeto.financeiro.dto.response.CentroDeCustoResponse;
import com.projeto.financeiro.dto.response.TituloResponse;
import com.projeto.financeiro.entity.CentroDeCusto;
import com.projeto.financeiro.entity.Titulo;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.security.TextSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TituloMapper {

    private final CentroDeCustoMapper centroDeCustoMapper;
    private final TextSanitizer textSanitizer;

    public TituloResponse toDto(Titulo entity) {
        if (entity == null) {
            return null;
        }

        List<CentroDeCustoResponse> centrosDeCusto = entity.getCentroDeCusto() != null
                ? entity.getCentroDeCusto().stream().map(centroDeCustoMapper::toDto).toList()
                : List.of();

        int qtdFat = entity.getFaturamentos() != null ? entity.getFaturamentos().size() : 0;

        return new TituloResponse(
                entity.getId(),
                entity.getDescricao(),
                entity.getValor(),
                entity.getDataVencimento(),
                entity.getTipo(),
                entity.getRecorrencia(),
                entity.getDataFim(),
                entity.isAtivo(),
                qtdFat,
                centrosDeCusto
        );
    }

    public Titulo toEntity(TituloRequest request, Usuario usuario, List<CentroDeCusto> centrosDeCusto) {
        if (request == null) {
            return null;
        }

        return Titulo.builder()
                .descricao(textSanitizer.sanitize(request.descricao()))
                .valor(request.valor())
                .dataVencimento(request.dataVencimento())
                .tipo(request.tipo())
                .recorrencia(request.recorrencia())
                .dataInicio(request.dataVencimento())
                .dataFim(request.dataFim())
                .ativo(true)
                .usuario(usuario)
                .centroDeCusto(centrosDeCusto != null ? new ArrayList<>(centrosDeCusto) : new ArrayList<>())
                .build();
    }

    public void updateEntity(Titulo entity, TituloRequest request, List<CentroDeCusto> centrosDeCusto) {
        if (entity == null || request == null) {
            return;
        }

        entity.setDescricao(textSanitizer.sanitize(request.descricao()));
        entity.setValor(request.valor());
        entity.setDataVencimento(request.dataVencimento());
        entity.setTipo(request.tipo());
        entity.setRecorrencia(request.recorrencia());
        entity.setDataInicio(request.dataVencimento());
        entity.setDataFim(request.dataFim());
        entity.setCentroDeCusto(centrosDeCusto != null ? new ArrayList<>(centrosDeCusto) : new ArrayList<>());
    }
}
