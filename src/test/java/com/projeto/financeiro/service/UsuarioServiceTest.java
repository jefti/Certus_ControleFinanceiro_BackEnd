package com.projeto.financeiro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.projeto.financeiro.dto.request.UsuarioRequest;
import com.projeto.financeiro.dto.response.UsuarioResponse;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.exception.ConflictException;
import com.projeto.financeiro.exception.NotFoundException;
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

    @Test
    void shouldThrowConflictExceptionWhenEmailAlreadyExists() {
        UsuarioRequest request = UsuarioFactory.buildUsuarioRequest(
                "johnDoe",
                "johnDoe@email.com",
                "123456",
                "99999999999");

        Usuario existingUser = UsuarioFactory.buildUsuario(
                1L,
                "johnDoe",
                "johnDoe@email.com",
                "123456",
                "99999999999",
                Instant.now(),
                null);

        when(usuarioRepository.findByEmail("johnDoe@email.com"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(ConflictException.class, () -> usuarioService.criar(request));
    }

    @Test
    void shouldThrowConflictExceptionWhenCelularAlreadyExists() {
        UsuarioRequest request = UsuarioFactory.buildUsuarioRequest(
                "johnDoe",
                "johnDoe@email2.com",
                "123456",
                "99999999999");

        Usuario existingUser = UsuarioFactory.buildUsuario(
                1L,
                "johnDoe",
                "johnDoe@email.com",
                "123456",
                "99999999999",
                Instant.now(),
                null);

        when(usuarioRepository.findByCelular("99999999999"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(ConflictException.class, () -> usuarioService.criar(request));
    }

    @Test
    void shouldCreateUserSuccessfully() {
        UsuarioRequest request = UsuarioFactory.buildUsuarioRequest(
                "johnDoe",
                "johnDoe@email.com",
                "123456",
                "99999999999");

        Usuario userToSave = UsuarioFactory.buildUsuario(
                null,
                "johnDoe",
                "johnDoe@email.com",
                "encoded-password",
                "99999999999",
                null,
                null);

        Usuario savedUser = UsuarioFactory.buildUsuario(
                1L,
                "johnDoe",
                "johnDoe@email.com",
                "encoded-password",
                "99999999999",
                Instant.now(),
                null);

        UsuarioResponse expectedResponse = new UsuarioResponse(
                1L,
                "johnDoe",
                "johnDoe@email.com",
                "99999999999",
                savedUser.getDataCriacao(),
                null);

        when(usuarioRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());

        when(usuarioRepository.findByCelular(request.celular()))
                .thenReturn(Optional.empty());

        when(usuarioMapper.toEntity(request))
                .thenReturn(userToSave);

        when(usuarioRepository.save(userToSave))
                .thenReturn(savedUser);

        when(usuarioMapper.toDto(savedUser))
                .thenReturn(expectedResponse);

        UsuarioResponse response = usuarioService.criar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("johnDoe", response.nome());
        assertEquals("johnDoe@email.com", response.email());
        assertEquals("99999999999", response.celular());
    }

    @Test
    void shouldReturnUserByIdSuccessfully() {
        long userId = 1L;

        Usuario user = UsuarioFactory.buildUsuario(
                userId,
                "johnDoe",
                "johnDoe@email.com",
                "123456",
                "99999999999",
                Instant.now(),
                null);

        UsuarioResponse expectedResponse = new UsuarioResponse(
                userId,
                "johnDoe",
                "johnDoe@email.com",
                "99999999999",
                user.getDataCriacao(),
                null);

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(user));
        when(usuarioMapper.toDto(user)).thenReturn(expectedResponse);

        UsuarioResponse response = usuarioService.buscarPorId(userId);

        assertNotNull(response);
        assertEquals(userId, response.id());
        assertEquals("johnDoe", response.nome());
        assertEquals("johnDoe@email.com", response.email());
        assertEquals("99999999999", response.celular());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserByIdDoesNotExist() {
        long userId = 99L;

        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> usuarioService.buscarPorId(userId));
    }

    @Test
    void shouldReturnAllUsersSuccessfully() {
        Usuario user1 = UsuarioFactory.buildUsuario(
                1L,
                "johnDoe",
                "johnDoe@email.com",
                "123456",
                "99999999999",
                Instant.now(),
                null);

        Usuario user2 = UsuarioFactory.buildUsuario(
                2L,
                "janeDoe",
                "janeDoe@email.com",
                "123456",
                "88888888888",
                Instant.now(),
                null);

        UsuarioResponse response1 = new UsuarioResponse(
                1L,
                "johnDoe",
                "johnDoe@email.com",
                "99999999999",
                user1.getDataCriacao(),
                null);

        UsuarioResponse response2 = new UsuarioResponse(
                2L,
                "janeDoe",
                "janeDoe@email.com",
                "88888888888",
                user2.getDataCriacao(),
                null);

        when(usuarioRepository.findAll()).thenReturn(List.of(user1, user2));
        when(usuarioMapper.toDto(user1)).thenReturn(response1);
        when(usuarioMapper.toDto(user2)).thenReturn(response2);

        List<UsuarioResponse> response = usuarioService.listarTodos();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("johnDoe@email.com", response.get(0).email());
        assertEquals("janeDoe@email.com", response.get(1).email());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdatingNonExistingUser() {
        long userId = 99L;

        UsuarioRequest request = UsuarioFactory.buildUsuarioRequest(
                "johnUpdated",
                "john.updated@email.com",
                "654321",
                "77777777777");

        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> usuarioService.atualizar(userId, request));
    }

    @Test
    void shouldThrowConflictExceptionWhenUpdatingWithExistingEmailFromAnotherUser() {
        long userId = 1L;

        UsuarioRequest request = UsuarioFactory.buildUsuarioRequest(
                "johnUpdated",
                "duplicated@email.com",
                "654321",
                "77777777777");

        Usuario existingUser = UsuarioFactory.buildUsuario(
                userId,
                "johnDoe",
                "johnDoe@email.com",
                "123456",
                "99999999999",
                Instant.now(),
                null);

        Usuario anotherUser = UsuarioFactory.buildUsuario(
                2L,
                "anotherUser",
                "duplicated@email.com",
                "123456",
                "88888888888",
                Instant.now(),
                null);

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(anotherUser));

        assertThrows(ConflictException.class, () -> usuarioService.atualizar(userId, request));
    }

    @Test
    void shouldThrowConflictExceptionWhenUpdatingWithExistingPhoneFromAnotherUser() {
        long userId = 1L;

        UsuarioRequest request = UsuarioFactory.buildUsuarioRequest(
                "johnUpdated",
                "john.updated@email.com",
                "654321",
                "77777777777");

        Usuario existingUser = UsuarioFactory.buildUsuario(
                userId,
                "johnDoe",
                "johnDoe@email.com",
                "123456",
                "99999999999",
                Instant.now(),
                null);

        Usuario anotherUser = UsuarioFactory.buildUsuario(
                2L,
                "anotherUser",
                "another@email.com",
                "123456",
                "77777777777",
                Instant.now(),
                null);

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(usuarioRepository.findByCelular(request.celular())).thenReturn(Optional.of(anotherUser));

        assertThrows(ConflictException.class, () -> usuarioService.atualizar(userId, request));
    }

    @Test
    void shouldInactivateUserSuccessfully() {
        long userId = 1L;

        Usuario existingUser = UsuarioFactory.buildUsuario(
                userId,
                "johnDoe",
                "johnDoe@email.com",
                "123456",
                "99999999999",
                Instant.now(),
                null);

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(usuarioRepository.save(existingUser)).thenReturn(existingUser);

        usuarioService.inativar(userId);

        assertNotNull(existingUser.getDataInativacao());
        verify(usuarioRepository).save(existingUser);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenInactivatingNonExistingUser() {
        long userId = 99L;

        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> usuarioService.inativar(userId));
    }

}
