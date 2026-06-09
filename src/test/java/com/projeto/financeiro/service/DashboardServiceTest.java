package com.projeto.financeiro.service;

import com.projeto.financeiro.dto.mapper.FaturamentoMapper;
import com.projeto.financeiro.dto.response.DashboardResponse;
import com.projeto.financeiro.dto.response.FaturamentoResponse;
import com.projeto.financeiro.entity.CentroDeCusto;
import com.projeto.financeiro.entity.Faturamento;
import com.projeto.financeiro.entity.Titulo;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.entity.enums.StatusTitulo;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private FaturamentoRepository faturamentoRepository;

    @Mock
    private TituloRepository tituloRepository;

    @Mock
    private CentroDeCustoRepository centroDeCustoRepository;

    @Mock
    private FaturamentoMapper faturamentoMapper;

    @InjectMocks
    private DashboardService dashboardService;

    private Usuario usuario;
    private Titulo tituloReceber;
    private Titulo tituloPagar;
    private Faturamento faturamentoReceber;
    private Faturamento faturamentoPagar;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nome("Jefti")
                .email("jefti@email.com")
                .senha("123")
                .celular("99999999999")
                .build();

        tituloReceber = Titulo.builder()
                .id(10L)
                .descricao("Salario")
                .tipo(TipoTitulo.RECEBER)
                .usuario(usuario)
                .build();

        tituloPagar = Titulo.builder()
                .id(20L)
                .descricao("Aluguel")
                .tipo(TipoTitulo.PAGAR)
                .usuario(usuario)
                .build();

        faturamentoReceber = Faturamento.builder()
                .id(100L)
                .titulo(tituloReceber)
                .dataVencimento(LocalDate.of(2026, 6, 5))
                .valor(new BigDecimal("5000.00"))
                .build();

        faturamentoPagar = Faturamento.builder()
                .id(200L)
                .titulo(tituloPagar)
                .dataVencimento(LocalDate.of(2026, 6, 5))
                .valor(new BigDecimal("1800.00"))
                .build();
    }

    @Test
    void shouldBuildDashboardSummaryFromPeriod() {
        SecurityContext securityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(usuarioAutenticado(usuario));
            when(faturamentoRepository.findByPeriodoAndUsuario(
                    LocalDate.of(2026, 6, 1),
                    LocalDate.of(2026, 6, 30),
                    usuario
            )).thenReturn(List.of(faturamentoReceber, faturamentoPagar));
            when(tituloRepository.findAllByUsuarioAndAtivo(usuario, true))
                    .thenReturn(List.of(tituloReceber, tituloPagar));
            when(centroDeCustoRepository.findByUsuario(usuario))
                    .thenReturn(List.of(new CentroDeCusto(), new CentroDeCusto(), new CentroDeCusto()));
            when(faturamentoMapper.toDto(faturamentoReceber)).thenReturn(new FaturamentoResponse(
                    100L,
                    10L,
                    "Salario",
                    TipoTitulo.RECEBER,
                    LocalDate.of(2026, 6, 5),
                    new BigDecimal("5000.00"),
                    null,
                    StatusTitulo.EM_ABERTO,
                    null
            ));
            when(faturamentoMapper.toDto(faturamentoPagar)).thenReturn(new FaturamentoResponse(
                    200L,
                    20L,
                    "Aluguel",
                    TipoTitulo.PAGAR,
                    LocalDate.of(2026, 6, 5),
                    new BigDecimal("1800.00"),
                    null,
                    StatusTitulo.EM_ABERTO,
                    null
            ));

            DashboardResponse result = dashboardService.obterDashboard(
                    LocalDate.of(2026, 6, 1),
                    LocalDate.of(2026, 6, 30)
            );

            assertEquals(new BigDecimal("5000.00"), result.totalReceitas());
            assertEquals(new BigDecimal("1800.00"), result.totalDespesas());
            assertEquals(new BigDecimal("3200.00"), result.saldo());
            assertEquals(2, result.quantidadeTitulosAtivos());
            assertEquals(3, result.quantidadeCentrosDeCusto());
            assertEquals(2, result.quantidadeLancamentos());
            assertEquals(1, result.serieFluxoCaixa().size());
            assertEquals(LocalDate.of(2026, 6, 5), result.serieFluxoCaixa().get(0).data());
            assertEquals(new BigDecimal("5000.00"), result.serieFluxoCaixa().get(0).totalReceitas());
            assertEquals(new BigDecimal("1800.00"), result.serieFluxoCaixa().get(0).totalDespesas());
            assertEquals(2, result.lancamentos().size());
        }
    }

    @Test
    void shouldGroupSeriesByDate() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Faturamento outroRecebimento = Faturamento.builder()
                .id(101L)
                .titulo(tituloReceber)
                .dataVencimento(LocalDate.of(2026, 6, 10))
                .valor(new BigDecimal("200.00"))
                .build();

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(usuarioAutenticado(usuario));
            when(faturamentoRepository.findByPeriodoAndUsuario(
                    LocalDate.of(2026, 6, 1),
                    LocalDate.of(2026, 6, 30),
                    usuario
            )).thenReturn(List.of(faturamentoReceber, faturamentoPagar, outroRecebimento));
            when(tituloRepository.findAllByUsuarioAndAtivo(usuario, true)).thenReturn(List.of());
            when(centroDeCustoRepository.findByUsuario(usuario)).thenReturn(List.of());
            when(faturamentoMapper.toDto(faturamentoReceber)).thenReturn(mock(FaturamentoResponse.class));
            when(faturamentoMapper.toDto(faturamentoPagar)).thenReturn(mock(FaturamentoResponse.class));
            when(faturamentoMapper.toDto(outroRecebimento)).thenReturn(mock(FaturamentoResponse.class));

            DashboardResponse result = dashboardService.obterDashboard(
                    LocalDate.of(2026, 6, 1),
                    LocalDate.of(2026, 6, 30)
            );

            assertEquals(2, result.serieFluxoCaixa().size());
            assertEquals(LocalDate.of(2026, 6, 5), result.serieFluxoCaixa().get(0).data());
            assertEquals(LocalDate.of(2026, 6, 10), result.serieFluxoCaixa().get(1).data());
        }
    }

    @Test
    void shouldRejectInvalidPeriod() {
        assertThrows(BadRequestException.class, () ->
                dashboardService.obterDashboard(
                        LocalDate.of(2026, 6, 30),
                        LocalDate.of(2026, 6, 1)
                )
        );
    }

    private org.springframework.security.core.Authentication usuarioAutenticado(Usuario usuario) {
        return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                usuario,
                null,
                usuario.getAuthorities()
        );
    }
}
