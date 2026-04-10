package com.projeto.financeiro.entity.enums;

import lombok.Getter;

@Getter
public enum TipoTitulo {
    PAGAR( "Pagar"),
    RECEBER("Receber");

    private final String descricao;

    TipoTitulo(String descricao) {
        this.descricao = descricao;
    }
}
