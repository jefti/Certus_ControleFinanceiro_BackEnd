package com.projeto.financeiro.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.entity.enums.Role;
import com.projeto.financeiro.factory.UsuarioFactory;
import com.projeto.financeiro.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AdminBootstrapTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AdminBootstrap adminBootstrap;

    @Test
    void shouldPromoteConfiguredUserToAdmin() {
        ReflectionTestUtils.setField(adminBootstrap, "adminEmails", "owner@email.com");
        Usuario user = UsuarioFactory.buildUsuario(
                1L, "owner", "owner@email.com", "123456", "99999999999", Instant.now(), null);

        when(usuarioRepository.findByEmail("owner@email.com")).thenReturn(Optional.of(user));

        adminBootstrap.run(null);

        assertEquals(Role.ADMIN, user.getRole());
        verify(usuarioRepository).save(user);
    }

    @Test
    void shouldNormalizeEmailBeforeLookup() {
        ReflectionTestUtils.setField(adminBootstrap, "adminEmails", "  Owner@Email.com  ");
        Usuario user = UsuarioFactory.buildUsuario(
                1L, "owner", "owner@email.com", "123456", "99999999999", Instant.now(), null);

        when(usuarioRepository.findByEmail("owner@email.com")).thenReturn(Optional.of(user));

        adminBootstrap.run(null);

        assertEquals(Role.ADMIN, user.getRole());
        verify(usuarioRepository).save(user);
    }

    @Test
    void shouldBeIdempotentWhenUserAlreadyAdmin() {
        ReflectionTestUtils.setField(adminBootstrap, "adminEmails", "owner@email.com");
        Usuario admin = UsuarioFactory.buildUsuario(
                1L, "owner", "owner@email.com", "123456", "99999999999", Instant.now(), null);
        admin.setRole(Role.ADMIN);

        when(usuarioRepository.findByEmail("owner@email.com")).thenReturn(Optional.of(admin));

        adminBootstrap.run(null);

        verify(usuarioRepository, never()).save(admin);
    }

    @Test
    void shouldSkipWhenConfiguredEmailNotFound() {
        ReflectionTestUtils.setField(adminBootstrap, "adminEmails", "ghost@email.com");
        when(usuarioRepository.findByEmail("ghost@email.com")).thenReturn(Optional.empty());

        adminBootstrap.run(null);

        verify(usuarioRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldDoNothingWhenNoEmailsConfigured() {
        ReflectionTestUtils.setField(adminBootstrap, "adminEmails", "");

        adminBootstrap.run(null);

        verifyNoInteractions(usuarioRepository);
    }
}
