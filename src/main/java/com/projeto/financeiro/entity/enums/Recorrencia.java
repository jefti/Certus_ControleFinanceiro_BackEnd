package com.projeto.financeiro.entity.enums;

import lombok.Getter;

@Getter
public enum Recorrencia {
    SEMANAL("Semanal"),
    MENSAL("Mensal"),
    BIMESTRAL("Bimestral"),
    TRIMESTRAL("Trimestral"),
    SEMESTRAL("Semestral"),
    ANUAL("Anual");


    private final String descricao;

    Recorrencia(String descricao) {
        this.descricao = descricao;
    }

}
