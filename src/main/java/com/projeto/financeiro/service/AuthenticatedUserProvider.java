package com.projeto.financeiro.service;

import com.projeto.financeiro.entity.Usuario;

public interface AuthenticatedUserProvider {
    Usuario getCurrentUser();
}
