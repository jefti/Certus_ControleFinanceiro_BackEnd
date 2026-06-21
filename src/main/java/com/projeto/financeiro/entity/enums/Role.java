package com.projeto.financeiro.entity.enums;

import lombok.Getter;

@Getter
public enum Role {
    USER("Usuario"),
    ADMIN("Administrador");

    private final String descricao;

    Role(String descricao) {
        this.descricao = descricao;
    }
}