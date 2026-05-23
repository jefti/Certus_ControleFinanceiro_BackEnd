package com.projeto.financeiro.service;

import com.projeto.financeiro.dto.mapper.FaturamentoMapper;
import com.projeto.financeiro.dto.request.ValidarFaturamentoRequest;
import com.projeto.financeiro.dto.response.FaturamentoResponse;
import com.projeto.financeiro.entity.Faturamento;
import com.projeto.financeiro.entity.Titulo;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.entity.enums.StatusTitulo;
import com.projeto.financeiro.entity.enums.TipoTitulo;
import com.projeto.financeiro.exception.BadRequestException;
import com.projeto.financeiro.exception.NotFoundException;
import com.projeto.financeiro.repository.FaturamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FaturamentoServiceTest {

    @Mock
    private FaturamentoRepository faturamentoRepository;

    @Mock
    private FaturamentoMapper faturamentoMapper;

    @InjectMocks
    private FaturamentoService faturamentoService;

    private Usuario usuario;
    private Titulo titulo;
    private Faturamento faturamento;
    private FaturamentoResponse response;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("Jefti")
                .email("jefti@email.com")
                .senha("123")
                .celular("99999999999")
                .build();

        titulo = Titulo.builder()
                .id(4L)
                .descricao("Aluguel")
                .valor(new BigDecimal("1200.00"))
                .tipo(TipoTitulo.PAGAR)
                .usuario(usuario)
                .build();

        faturamento = Faturamento.builder()
                .id(14L)
                .titulo(titulo)
                .dataVencimento(LocalDate.of(2026, 6, 1))
                .valor(new BigDecimal("1200.00"))
                .dataPagamento(null)
                .observacao(null)
                .build();

        response = new FaturamentoResponse(
                14L,
                4L,
                "Aluguel",
                TipoTitulo.PAGAR,
                LocalDate.of(2026, 6, 1),
                new BigDecimal("1200.00"),
                null,
                StatusTitulo.EM_ABERTO,
                null
        );
    }

    @Test
    void shouldListFaturamentosByTitulo() {
        SecurityContext securityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(usuarioAutenticado(usuario));
            when(faturamentoRepository.findByTituloIdAndUsuario(4L, usuario)).thenReturn(List.of(faturamento));
            when(faturamentoMapper.toDto(faturamento)).thenReturn(response);

            List<FaturamentoResponse> result = faturamentoService.listarPorTitulo(4L);

            assertEquals(1, result.size());
            assertEquals(14L, result.get(0).id());
            assertEquals("Aluguel", result.get(0).tituloDescricao());
        }
    }

    @Test
    void shouldReturnFaturamentoById() {
        SecurityContext securityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(usuarioAutenticado(usuario));
            when(faturamentoRepository.findByIdAndUsuario(14L, usuario)).thenReturn(Optional.of(faturamento));
            when(faturamentoMapper.toDto(faturamento)).thenReturn(response);

            FaturamentoResponse result = faturamentoService.buscarPorId(14L);

            assertEquals(14L, result.id());
            assertEquals(4L, result.tituloId());
        }
    }

    @Test
    void shouldThrowNotFoundWhenFaturamentoDoesNotExist() {
        SecurityContext securityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(usuarioAutenticado(usuario));
            when(faturamentoRepository.findByIdAndUsuario(99L, usuario)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> faturamentoService.buscarPorId(99L));
        }
    }

    @Test
    void shouldValidateFaturamentoWithProvidedDate() {
        SecurityContext securityContext = mock(SecurityContext.class);
        LocalDateTime dataPagamento = LocalDateTime.of(2026, 6, 2, 10, 0, 0);
        ValidarFaturamentoRequest request = new ValidarFaturamentoRequest(dataPagamento, "Pago via Pix");

        FaturamentoResponse validatedResponse = new FaturamentoResponse(
                14L,
                4L,
                "Aluguel",
                TipoTitulo.PAGAR,
                LocalDate.of(2026, 6, 1),
                new BigDecimal("1200.00"),
                dataPagamento,
                StatusTitulo.PAGO,
                "Pago via Pix"
        );

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(usuarioAutenticado(usuario));
            when(faturamentoRepository.findByIdAndUsuario(14L, usuario)).thenReturn(Optional.of(faturamento));
            when(faturamentoRepository.save(any(Faturamento.class))).thenReturn(faturamento);
            when(faturamentoMapper.toDto(any(Faturamento.class))).thenReturn(validatedResponse);

            FaturamentoResponse result = faturamentoService.validar(14L, request);

            assertEquals(StatusTitulo.PAGO, result.status());
            assertEquals(dataPagamento, result.dataPagamento());
            assertEquals("Pago via Pix", result.observacao());
            verify(faturamentoRepository).save(faturamento);
        }
    }

    @Test
    void shouldThrowBadRequestWhenFaturamentoAlreadyValidated() {
        SecurityContext securityContext = mock(SecurityContext.class);
        faturamento.setDataPagamento(LocalDateTime.now());

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(usuarioAutenticado(usuario));
            when(faturamentoRepository.findByIdAndUsuario(14L, usuario)).thenReturn(Optional.of(faturamento));

            assertThrows(BadRequestException.class, () ->
                    faturamentoService.validar(14L, new ValidarFaturamentoRequest(null, null))
            );
        }
    }

    private org.springframework.security.core.Authentication usuarioAutenticado(Usuario usuario) {
        return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                usuario,
                null,
                usuario.getAuthorities()
        );
    }
}
