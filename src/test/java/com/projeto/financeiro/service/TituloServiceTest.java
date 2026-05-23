package com.projeto.financeiro.service;

import com.projeto.financeiro.dto.mapper.TituloMapper;
import com.projeto.financeiro.dto.request.TituloRequest;
import com.projeto.financeiro.entity.Faturamento;
import com.projeto.financeiro.entity.Titulo;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.entity.enums.Recorrencia;
import com.projeto.financeiro.entity.enums.TipoTitulo;
import com.projeto.financeiro.exception.BadRequestException;
import com.projeto.financeiro.repository.CentroDeCustoRepository;
import com.projeto.financeiro.repository.FaturamentoRepository;
import com.projeto.financeiro.repository.TituloRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TituloServiceTest {

    @Mock
    private TituloRepository tituloRepository;

    @Mock
    private TituloMapper tituloMapper;

    @Mock
    private CentroDeCustoRepository centroDeCustoRepository;

    @Mock
    private FaturamentoRepository faturamentoRepository;

    @Mock
    private RecorrenciaCalculator recorrenciaCalculator;

    @InjectMocks
    private TituloService tituloService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("Jefti")
                .email("jefti@email.com")
                .senha("123")
                .celular("99999999999")
                .build();
    }

    @Test
    void shouldRejectRecurringTituloWithoutDataFim() {
        TituloRequest request = new TituloRequest(
                null,
                "Salario",
                new BigDecimal("5000.00"),
                LocalDate.of(2026, 6, 1),
                TipoTitulo.RECEBER,
                Recorrencia.MENSAL,
                null,
                List.of()
        );

        assertThrows(BadRequestException.class, () -> tituloService.criar(request));
    }

    @Test
    void shouldGenerateFaturamentosWhenCreatingTitulo() {
        TituloRequest request = new TituloRequest(
                null,
                "Aluguel",
                new BigDecimal("1000.00"),
                LocalDate.of(2026, 6, 1),
                TipoTitulo.PAGAR,
                Recorrencia.MENSAL,
                LocalDate.of(2026, 8, 1),
                List.of()
        );

        Titulo titulo = Titulo.builder()
                .id(10L)
                .descricao("Aluguel")
                .valor(new BigDecimal("1000.00"))
                .tipo(TipoTitulo.PAGAR)
                .recorrencia(Recorrencia.MENSAL)
                .dataInicio(LocalDate.of(2026, 6, 1))
                .dataFim(LocalDate.of(2026, 8, 1))
                .usuario(usuario)
                .centroDeCusto(List.of())
                .build();

        SecurityContext securityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(usuarioAutenticado(usuario));
            when(tituloMapper.toEntity(request, usuario, List.of())).thenReturn(titulo);
            when(tituloRepository.save(any(Titulo.class))).thenReturn(titulo);
            when(recorrenciaCalculator.gerarDatas(
                    LocalDate.of(2026, 6, 1),
                    LocalDate.of(2026, 8, 1),
                    Recorrencia.MENSAL
            )).thenReturn(List.of(
                    LocalDate.of(2026, 6, 1),
                    LocalDate.of(2026, 7, 1),
                    LocalDate.of(2026, 8, 1)
            ));
            when(faturamentoRepository.existsByTituloAndDataVencimento(any(Titulo.class), any(LocalDate.class)))
                    .thenReturn(false);

            tituloService.criar(request);

            verify(faturamentoRepository, times(3)).save(any(Faturamento.class));
        }
    }

    @Test
    void shouldDeleteOnlyUnpaidFaturamentosWhenUpdatingTitulo() {
        TituloRequest request = new TituloRequest(
                null,
                "Aluguel Atualizado",
                new BigDecimal("1200.00"),
                LocalDate.of(2026, 6, 1),
                TipoTitulo.PAGAR,
                Recorrencia.MENSAL,
                LocalDate.of(2026, 7, 1),
                List.of()
        );

        Titulo titulo = Titulo.builder()
                .id(10L)
                .descricao("Aluguel")
                .valor(new BigDecimal("1000.00"))
                .tipo(TipoTitulo.PAGAR)
                .recorrencia(Recorrencia.MENSAL)
                .dataInicio(LocalDate.of(2026, 6, 1))
                .dataFim(LocalDate.of(2026, 7, 1))
                .usuario(usuario)
                .ativo(true)
                .centroDeCusto(List.of())
                .build();

        Faturamento naoPago = Faturamento.builder()
                .id(1L)
                .titulo(titulo)
                .dataVencimento(LocalDate.of(2026, 7, 1))
                .valor(new BigDecimal("1000.00"))
                .build();

        SecurityContext securityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(usuarioAutenticado(usuario));
            when(tituloRepository.findByIdAndUsuario(10L, usuario)).thenReturn(Optional.of(titulo));
            when(faturamentoRepository.findByTituloAndDataPagamentoIsNull(titulo)).thenReturn(List.of(naoPago));
            when(tituloRepository.save(any(Titulo.class))).thenReturn(titulo);
            when(recorrenciaCalculator.gerarDatas(any(LocalDate.class), any(LocalDate.class), any(Recorrencia.class)))
                    .thenReturn(List.of(LocalDate.of(2026, 6, 1), LocalDate.of(2026, 7, 1)));
            when(faturamentoRepository.existsByTituloAndDataVencimento(any(Titulo.class), any(LocalDate.class)))
                    .thenReturn(false);

            tituloService.atualizar(10L, request);

            verify(faturamentoRepository).deleteAll(List.of(naoPago));
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
