package com.projeto.financeiro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.projeto.financeiro.dto.mapper.UsuarioMapper;
import com.projeto.financeiro.dto.request.UsuarioCreateRequest;
import com.projeto.financeiro.dto.request.UsuarioUpdateRequest;
import com.projeto.financeiro.dto.response.UsuarioResponse;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.exception.ConflictException;
import com.projeto.financeiro.factory.UsuarioFactory;
import com.projeto.financeiro.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    // ---------- cadastro (inalterado) ----------

    @Test
    void shouldThrowConflictExceptionWhenEmailAlreadyExists() {
        UsuarioCreateRequest request = UsuarioFactory.buildUsuarioCreateRequest(
                "johnDoe", "johnDoe@email.com", "12345678", "99999999999");

        Usuario existingUser = UsuarioFactory.buildUsuario(
                1L, "johnDoe", "johnDoe@email.com", "123456", "99999999999", Instant.now(), null);

        when(usuarioRepository.findByEmail("johnDoe@email.com"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(ConflictException.class, () -> usuarioService.criar(request));
    }

    @Test
    void shouldThrowConflictExceptionWhenCelularAlreadyExists() {
        UsuarioCreateRequest request = UsuarioFactory.buildUsuarioCreateRequest(
                "johnDoe", "johnDoe@email2.com", "12345678", "99999999999");

        Usuario existingUser = UsuarioFactory.buildUsuario(
                1L, "johnDoe", "johnDoe@email.com", "123456", "99999999999", Instant.now(), null);

        when(usuarioRepository.findByCelular("99999999999"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(ConflictException.class, () -> usuarioService.criar(request));
    }

    @Test
    void shouldCreateUserSuccessfully() {
        UsuarioCreateRequest request = UsuarioFactory.buildUsuarioCreateRequest(
                "johnDoe", "johnDoe@email.com", "12345678", "99999999999");

        Usuario userToSave = UsuarioFactory.buildUsuario(
                null, "johnDoe", "johnDoe@email.com", "encoded-password", "99999999999", null, null);

        Usuario savedUser = UsuarioFactory.buildUsuario(
                1L, "johnDoe", "johnDoe@email.com", "encoded-password", "99999999999", Instant.now(), null);

        UsuarioResponse expectedResponse = new UsuarioResponse(
                1L, "johnDoe", "johnDoe@email.com", "99999999999", savedUser.getDataCriacao(), null);

        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(usuarioRepository.findByCelular(request.celular())).thenReturn(Optional.empty());
        when(usuarioMapper.toEntity(request)).thenReturn(userToSave);
        when(usuarioRepository.save(userToSave)).thenReturn(savedUser);
        when(usuarioMapper.toDto(savedUser)).thenReturn(expectedResponse);

        UsuarioResponse response = usuarioService.criar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("johnDoe@email.com", response.email());
    }

    // ---------- perfil proprio (/me) ----------

    @Test
    void shouldReturnAuthenticatedUserProfile() {
        Usuario logado = UsuarioFactory.buildUsuario(
                1L, "johnDoe", "johnDoe@email.com", "123456", "99999999999", Instant.now(), null);

        UsuarioResponse expected = new UsuarioResponse(
                1L, "johnDoe", "johnDoe@email.com", "99999999999", logado.getDataCriacao(), null);

        SecurityContext securityContext = mock(SecurityContext.class);
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(autenticado(logado));
            when(usuarioMapper.toDto(logado)).thenReturn(expected);

            UsuarioResponse response = usuarioService.buscarMeuPerfil();

            assertNotNull(response);
            assertEquals(1L, response.id());
            assertEquals("johnDoe@email.com", response.email());
        }
    }

    @Test
    void shouldThrowAccessDeniedWhenNotAuthenticated() {
        SecurityContext securityContext = mock(SecurityContext.class);
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            assertThrows(AccessDeniedException.class, () -> usuarioService.buscarMeuPerfil());
        }
    }

    @Test
    void shouldUpdateAuthenticatedUserSuccessfully() {
        Usuario logado = UsuarioFactory.buildUsuario(
                1L, "johnDoe", "johnDoe@email.com", "123456", "99999999999", Instant.now(), null);

        UsuarioUpdateRequest request = UsuarioFactory.buildUsuarioUpdateRequest(
                "johnUpdated", "john.updated@email.com", "87654321", "77777777777");

        UsuarioResponse expected = new UsuarioResponse(
                1L, "johnUpdated", "john.updated@email.com", "77777777777", logado.getDataCriacao(), null);

        SecurityContext securityContext = mock(SecurityContext.class);
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(autenticado(logado));
            when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.empty());
            when(usuarioRepository.findByCelular(request.celular())).thenReturn(Optional.empty());
            when(usuarioRepository.save(logado)).thenReturn(logado);
            when(usuarioMapper.toDto(logado)).thenReturn(expected);

            UsuarioResponse response = usuarioService.atualizar(request);

            assertNotNull(response);
            assertEquals("john.updated@email.com", response.email());
            verify(usuarioMapper).updateEntity(logado, request);
            verify(usuarioRepository).save(logado);
        }
    }

    @Test
    void shouldThrowConflictWhenUpdatingWithEmailFromAnotherUser() {
        Usuario logado = UsuarioFactory.buildUsuario(
                1L, "johnDoe", "johnDoe@email.com", "123456", "99999999999", Instant.now(), null);

        Usuario outroUsuario = UsuarioFactory.buildUsuario(
                2L, "anotherUser", "duplicated@email.com", "123456", "88888888888", Instant.now(), null);

        UsuarioUpdateRequest request = UsuarioFactory.buildUsuarioUpdateRequest(
                "johnUpdated", "duplicated@email.com", "87654321", "77777777777");

        SecurityContext securityContext = mock(SecurityContext.class);
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(autenticado(logado));
            when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(outroUsuario));

            assertThrows(ConflictException.class, () -> usuarioService.atualizar(request));
        }
    }

    @Test
    void shouldThrowConflictWhenUpdatingWithPhoneFromAnotherUser() {
        Usuario logado = UsuarioFactory.buildUsuario(
                1L, "johnDoe", "johnDoe@email.com", "123456", "99999999999", Instant.now(), null);

        Usuario outroUsuario = UsuarioFactory.buildUsuario(
                2L, "anotherUser", "another@email.com", "123456", "77777777777", Instant.now(), null);

        UsuarioUpdateRequest request = UsuarioFactory.buildUsuarioUpdateRequest(
                "johnUpdated", "john.updated@email.com", "87654321", "77777777777");

        SecurityContext securityContext = mock(SecurityContext.class);
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(autenticado(logado));
            when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.empty());
            when(usuarioRepository.findByCelular(request.celular())).thenReturn(Optional.of(outroUsuario));

            assertThrows(ConflictException.class, () -> usuarioService.atualizar(request));
        }
    }

    @Test
    void shouldInactivateAuthenticatedUser() {
        Usuario logado = UsuarioFactory.buildUsuario(
                1L, "johnDoe", "johnDoe@email.com", "123456", "99999999999", Instant.now(), null);

        SecurityContext securityContext = mock(SecurityContext.class);
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(autenticado(logado));
            when(usuarioRepository.save(logado)).thenReturn(logado);

            usuarioService.inativar();

            assertNotNull(logado.getDataInativacao());
            verify(usuarioRepository).save(logado);
        }
    }

    private Authentication autenticado(Usuario usuario) {
        return new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
    }
}
