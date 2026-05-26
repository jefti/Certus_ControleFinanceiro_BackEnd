package com.projeto.financeiro.service;

import com.projeto.financeiro.dto.mapper.CentroDeCustoMapper;
import com.projeto.financeiro.dto.request.CentroDeCustoRequest;
import com.projeto.financeiro.dto.response.CentroDeCustoResponse;
import com.projeto.financeiro.entity.CentroDeCusto;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.exception.BadRequestException;
import com.projeto.financeiro.exception.ConflictException;
import com.projeto.financeiro.exception.NotFoundException;
import com.projeto.financeiro.repository.CentroDeCustoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CentroDeCustoServiceTest {

    @Mock
    private CentroDeCustoRepository centroDeCustoRepository;

    @Mock
    private CentroDeCustoMapper centroDeCustoMapper;

    @InjectMocks
    private CentroDeCustoService centroDeCustoService;

    private Usuario usuario;
    private CentroDeCusto centroDeCusto;
    private CentroDeCustoResponse response;
    private CentroDeCustoRequest request;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Marcelo");
        usuario.setEmail("marcelo@email.com");
        usuario.setSenha("senha123");
        usuario.setCelular("11999999999");
        usuario.setDataCriacao(Instant.now());

        centroDeCusto = new CentroDeCusto();
        centroDeCusto.setId(1L);
        centroDeCusto.setDescricao("Alimentação");
        centroDeCusto.setObservacao("Gastos com alimentação");
        centroDeCusto.setUsuario(usuario);

        response = new CentroDeCustoResponse(1L, "Alimentação", "Gastos com alimentação");
        request = new CentroDeCustoRequest(null, "Alimentação", "Gastos com alimentação");
    }

    @Test
    void shouldCreateCentroDeCustoSuccessfully() {
        SecurityContext securityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(autenticado(usuario));
            when(centroDeCustoRepository.existsByDescricaoAndUsuario("Alimentação", usuario)).thenReturn(false);
            when(centroDeCustoMapper.toEntity(request, usuario)).thenReturn(centroDeCusto);
            when(centroDeCustoRepository.save(centroDeCusto)).thenReturn(centroDeCusto);
            when(centroDeCustoMapper.toDto(centroDeCusto)).thenReturn(response);

            CentroDeCustoResponse result = centroDeCustoService.criar(request);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("Alimentação", result.descricao());
        }
    }

    @Test
    void shouldThrowConflictWhenDescricaoAlreadyExists() {
        SecurityContext securityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(autenticado(usuario));
            when(centroDeCustoRepository.existsByDescricaoAndUsuario("Alimentação", usuario)).thenReturn(true);

            assertThrows(ConflictException.class, () -> centroDeCustoService.criar(request));
        }
    }

    @Test
    void shouldThrowBadRequestWhenDescricaoIsBlank() {
        CentroDeCustoRequest requestBlank = new CentroDeCustoRequest(null, "", null);

        assertThrows(BadRequestException.class, () -> centroDeCustoService.criar(requestBlank));
    }

    @Test
    void shouldThrowBadRequestWhenDescricaoIsNull() {
        CentroDeCustoRequest requestNull = new CentroDeCustoRequest(null, null, null);

        assertThrows(BadRequestException.class, () -> centroDeCustoService.criar(requestNull));
    }

    @Test
    void shouldListAllCentrosDeCustoForUser() {
        SecurityContext securityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(autenticado(usuario));
            when(centroDeCustoRepository.findByUsuario(usuario)).thenReturn(List.of(centroDeCusto));
            when(centroDeCustoMapper.toDto(centroDeCusto)).thenReturn(response);

            List<CentroDeCustoResponse> result = centroDeCustoService.listarTodos();

            assertEquals(1, result.size());
            assertEquals("Alimentação", result.get(0).descricao());
        }
    }

    @Test
    void shouldReturnCentroDeCustoById() {
        SecurityContext securityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(autenticado(usuario));
            when(centroDeCustoRepository.findByIdAndUsuario(1L, usuario)).thenReturn(Optional.of(centroDeCusto));
            when(centroDeCustoMapper.toDto(centroDeCusto)).thenReturn(response);

            CentroDeCustoResponse result = centroDeCustoService.buscarPorId(1L);

            assertNotNull(result);
            assertEquals(1L, result.id());
        }
    }

    @Test
    void shouldThrowNotFoundWhenCentroDeCustoNotFound() {
        SecurityContext securityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(autenticado(usuario));
            when(centroDeCustoRepository.findByIdAndUsuario(99L, usuario)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> centroDeCustoService.buscarPorId(99L));
        }
    }

    @Test
    void shouldUpdateCentroDeCustoSuccessfully() {
        SecurityContext securityContext = mock(SecurityContext.class);
        CentroDeCustoRequest updateRequest = new CentroDeCustoRequest(1L, "Transporte", "Gastos com transporte");
        CentroDeCustoResponse updatedResponse = new CentroDeCustoResponse(1L, "Transporte", "Gastos com transporte");

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(autenticado(usuario));
            when(centroDeCustoRepository.findByIdAndUsuario(1L, usuario)).thenReturn(Optional.of(centroDeCusto));
            when(centroDeCustoRepository.save(centroDeCusto)).thenReturn(centroDeCusto);
            when(centroDeCustoMapper.toDto(centroDeCusto)).thenReturn(updatedResponse);

            CentroDeCustoResponse result = centroDeCustoService.atualizar(1L, updateRequest);

            assertNotNull(result);
            assertEquals("Transporte", result.descricao());
            verify(centroDeCustoMapper).updateEntity(centroDeCusto, updateRequest);
        }
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingNonExistingCentroDeCusto() {
        SecurityContext securityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(autenticado(usuario));
            when(centroDeCustoRepository.findByIdAndUsuario(99L, usuario)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> centroDeCustoService.atualizar(99L, request));
        }
    }

    @Test
    void shouldDeleteCentroDeCustoSuccessfully() {
        SecurityContext securityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(autenticado(usuario));
            when(centroDeCustoRepository.findByIdAndUsuario(1L, usuario)).thenReturn(Optional.of(centroDeCusto));

            centroDeCustoService.inativar(1L);

            verify(centroDeCustoRepository).delete(centroDeCusto);
        }
    }

    @Test
    void shouldThrowNotFoundWhenDeletingNonExistingCentroDeCusto() {
        SecurityContext securityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(autenticado(usuario));
            when(centroDeCustoRepository.findByIdAndUsuario(99L, usuario)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> centroDeCustoService.inativar(99L));
        }
    }

    private Authentication autenticado(Usuario usuario) {
        return new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
    }
}
