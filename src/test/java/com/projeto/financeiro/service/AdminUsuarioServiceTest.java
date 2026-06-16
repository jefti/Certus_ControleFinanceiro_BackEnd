package com.projeto.financeiro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projeto.financeiro.dto.mapper.UsuarioMapper;
import com.projeto.financeiro.dto.response.UsuarioResponse;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.entity.enums.Role;
import com.projeto.financeiro.exception.BadRequestException;
import com.projeto.financeiro.exception.NotFoundException;
import com.projeto.financeiro.factory.UsuarioFactory;
import com.projeto.financeiro.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AdminUsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private AdminUsuarioService adminUsuarioService;

    @Test
    void shouldListAllUsers() {
        Usuario user1 = UsuarioFactory.buildUsuario(
                1L, "johnDoe", "johnDoe@email.com", "123456", "99999999999", Instant.now(), null);
        Usuario user2 = UsuarioFactory.buildUsuario(
                2L, "janeDoe", "janeDoe@email.com", "123456", "88888888888", Instant.now(), null);

        UsuarioResponse r1 = new UsuarioResponse(1L, "johnDoe", "johnDoe@email.com", "99999999999", user1.getDataCriacao(), null);
        UsuarioResponse r2 = new UsuarioResponse(2L, "janeDoe", "janeDoe@email.com", "88888888888", user2.getDataCriacao(), null);

        when(usuarioRepository.findAll()).thenReturn(List.of(user1, user2));
        when(usuarioMapper.toDto(user1)).thenReturn(r1);
        when(usuarioMapper.toDto(user2)).thenReturn(r2);

        List<UsuarioResponse> response = adminUsuarioService.listarUsuarios();

        assertEquals(2, response.size());
        assertEquals("johnDoe@email.com", response.get(0).email());
        assertEquals("janeDoe@email.com", response.get(1).email());
    }

    @Test
    void shouldReturnUserById() {
        Usuario user = UsuarioFactory.buildUsuario(
                1L, "johnDoe", "johnDoe@email.com", "123456", "99999999999", Instant.now(), null);
        UsuarioResponse expected = new UsuarioResponse(
                1L, "johnDoe", "johnDoe@email.com", "99999999999", user.getDataCriacao(), null);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user));
        when(usuarioMapper.toDto(user)).thenReturn(expected);

        UsuarioResponse response = adminUsuarioService.buscarPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
    }

    @Test
    void shouldThrowNotFoundWhenUserByIdMissing() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> adminUsuarioService.buscarPorId(99L));
    }

    @Test
    void shouldDeactivateUser() {
        Usuario user = UsuarioFactory.buildUsuario(
                1L, "johnDoe", "johnDoe@email.com", "123456", "99999999999", Instant.now(), null);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user));
        when(usuarioRepository.save(user)).thenReturn(user);

        adminUsuarioService.desativar(1L);

        assertNotNull(user.getDataInativacao());
        verify(usuarioRepository).save(user);
    }

    @Test
    void shouldNotDeactivateAdminAccount() {
        Usuario admin = UsuarioFactory.buildUsuario(
                1L, "owner", "owner@email.com", "123456", "99999999999", Instant.now(), null);
        admin.setRole(Role.ADMIN);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThrows(BadRequestException.class, () -> adminUsuarioService.desativar(1L));
        verify(usuarioRepository, never()).save(admin);
    }

    @Test
    void shouldBeIdempotentWhenDeactivatingAlreadyInactiveUser() {
        Usuario user = UsuarioFactory.buildUsuario(
                1L, "johnDoe", "johnDoe@email.com", "123456", "99999999999", Instant.now(), Instant.now());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user));

        adminUsuarioService.desativar(1L);

        verify(usuarioRepository, never()).save(user);
    }

    @Test
    void shouldReactivateUser() {
        Usuario user = UsuarioFactory.buildUsuario(
                1L, "johnDoe", "johnDoe@email.com", "123456", "99999999999", Instant.now(), Instant.now());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user));
        when(usuarioRepository.save(user)).thenReturn(user);

        adminUsuarioService.reativar(1L);

        assertNull(user.getDataInativacao());
        verify(usuarioRepository).save(user);
    }

    @Test
    void shouldBeIdempotentWhenReactivatingActiveUser() {
        Usuario user = UsuarioFactory.buildUsuario(
                1L, "johnDoe", "johnDoe@email.com", "123456", "99999999999", Instant.now(), null);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user));

        adminUsuarioService.reativar(1L);

        verify(usuarioRepository, never()).save(user);
    }
}
