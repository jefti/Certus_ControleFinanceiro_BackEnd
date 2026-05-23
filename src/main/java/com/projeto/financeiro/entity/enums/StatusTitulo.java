package com.projeto.financeiro.entity.enums;

import lombok.Getter;

@Getter
public enum StatusTitulo {
    EM_ABERTO("Em aberto"),
    PAGO("Pago"),
    ATRASADO("Atrasado");

    private final String descricao;

    StatusTitulo(String descricao) {
        this.descricao = descricao;
    }

}
