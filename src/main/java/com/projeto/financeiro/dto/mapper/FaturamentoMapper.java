package com.projeto.financeiro.dto.mapper;

import com.projeto.financeiro.dto.response.FaturamentoResponse;
import com.projeto.financeiro.entity.Faturamento;
import com.projeto.financeiro.entity.Titulo;
import org.springframework.stereotype.Component;

@Component
public class FaturamentoMapper {

    public FaturamentoResponse toDto(Faturamento f) {
        if (f == null) return null;
        Titulo t = f.getTitulo();
        return new FaturamentoResponse(
                f.getId(),
                t != null ? t.getId() : null,
                t != null ? t.getDescricao() : null,
                t != null ? t.getTipo() : null,
                f.getDataVencimento(),
                f.getValor(),
                f.getDataPagamento(),
                f.getStatus(),
                f.getObservacao()
        );
    }

    public Faturamento toEntity(FaturamentoResponse f) {
        if (f == null) return null;
        return Faturamento.builder()
                .id(f.id())
                .dataVencimento(f.dataVencimento())
                .valor(f.valor())
                .dataPagamento(f.dataPagamento())
                .observacao(f.observacao())
                .build();
    }
}
