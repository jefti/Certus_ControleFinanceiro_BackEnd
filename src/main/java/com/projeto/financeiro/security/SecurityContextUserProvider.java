package com.projeto.financeiro.security;

import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.service.AuthenticatedUserProvider;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextUserProvider implements AuthenticatedUserProvider {

    @Override
    public Usuario getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Usuário não autenticado");
        }
        return usuario;
    }
}
