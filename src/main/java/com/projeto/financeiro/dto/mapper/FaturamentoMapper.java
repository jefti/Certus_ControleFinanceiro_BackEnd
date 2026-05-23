package com.projeto.financeiro.dto.mapper;

import com.projeto.financeiro.dto.response.FaturamentoResponse;
import com.projeto.financeiro.entity.Faturamento;
import com.projeto.financeiro.entity.Titulo;
import org.springframework.stereotype.Component;

@Component
public class FaturamentoMapper {

    public FaturamentoResponse toDto(Faturamento faturamento) {
        if (faturamento == null) {
            return null;
        }

        Titulo titulo = faturamento.getTitulo();

        return new FaturamentoResponse(
                faturamento.getId(),
                titulo != null ? titulo.getId() : null,
                titulo != null ? titulo.getDescricao() : null,
                titulo != null ? titulo.getTipo() : null,
                faturamento.getDataVencimento(),
                faturamento.getValor(),
                faturamento.getDataPagamento(),
                faturamento.getStatus(),
                faturamento.getObservacao()
        );
    }
}