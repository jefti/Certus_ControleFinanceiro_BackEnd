package com.projeto.financeiro.dto.mapper;

import com.projeto.financeiro.dto.request.TituloRequest;
import com.projeto.financeiro.dto.response.TituloResponse;
import com.projeto.financeiro.entity.CentroDeCusto;
import com.projeto.financeiro.entity.Titulo;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.entity.enums.Recorrencia;
import com.projeto.financeiro.entity.enums.TipoTitulo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TituloMapperTest {

    private TituloMapper mapper;
    private CentroDeCustoMapper centroDeCustoMapper;

    private Usuario usuario;
    private Titulo titulo;

    @BeforeEach
    void setUp() {
        centroDeCustoMapper = new CentroDeCustoMapper();
        mapper = new TituloMapper(centroDeCustoMapper);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Marcelo");

        titulo = Titulo.builder()
                .id(10L)
                .descricao("Aluguel")
                .valor(new BigDecimal("1500.00"))
                .dataVencimento(LocalDate.of(2026, 6, 1))
                .tipo(TipoTitulo.PAGAR)
                .recorrencia(Recorrencia.MENSAL)
                .dataInicio(LocalDate.of(2026, 6, 1))
                .dataFim(LocalDate.of(2026, 12, 1))
                .ativo(true)
                .usuario(usuario)
                .centroDeCusto(List.of())
                .build();
    }

    @Test
    void shouldMapEntityToDto() {
        TituloResponse dto = mapper.toDto(titulo);

        assertNotNull(dto);
        assertEquals(10L, dto.id());
        assertEquals("Aluguel", dto.descricao());
        assertEquals(new BigDecimal("1500.00"), dto.valor());
        assertEquals(TipoTitulo.PAGAR, dto.tipo());
        assertEquals(Recorrencia.MENSAL, dto.recorrencia());
        assertTrue(dto.ativo());
    }

    @Test
    void shouldReturnNullToDtoWhenEntityIsNull() {
        assertNull(mapper.toDto(null));
    }

    @Test
    void shouldMapRequestToEntity() {
        TituloRequest request = new TituloRequest(
                null,
                "Salário",
                new BigDecimal("5000.00"),
                LocalDate.of(2026, 6, 1),
                TipoTitulo.RECEBER,
                null,
                null,
                List.of()
        );

        Titulo result = mapper.toEntity(request, usuario, List.of());

        assertNotNull(result);
        assertEquals("Salário", result.getDescricao());
        assertEquals(new BigDecimal("5000.00"), result.getValor());
        assertEquals(TipoTitulo.RECEBER, result.getTipo());
        assertEquals(usuario, result.getUsuario());
        assertTrue(result.isAtivo());
    }

    @Test
    void shouldReturnNullToEntityWhenRequestIsNull() {
        assertNull(mapper.toEntity(null, usuario, List.of()));
    }

    @Test
    void shouldHandleNullCentrosDeCustoInToEntity() {
        TituloRequest request = new TituloRequest(
                null, "Teste", new BigDecimal("100.00"),
                LocalDate.of(2026, 6, 1), TipoTitulo.PAGAR,
                null, null, null
        );

        Titulo result = mapper.toEntity(request, usuario, null);

        assertNotNull(result);
        assertTrue(result.getCentroDeCusto().isEmpty());
    }

    @Test
    void shouldUpdateEntityFromRequest() {
        TituloRequest request = new TituloRequest(
                10L,
                "Aluguel Atualizado",
                new BigDecimal("1800.00"),
                LocalDate.of(2026, 7, 1),
                TipoTitulo.PAGAR,
                Recorrencia.MENSAL,
                LocalDate.of(2026, 12, 1),
                List.of()
        );

        mapper.updateEntity(titulo, request, List.of());

        assertEquals("Aluguel Atualizado", titulo.getDescricao());
        assertEquals(new BigDecimal("1800.00"), titulo.getValor());
        assertEquals(LocalDate.of(2026, 7, 1), titulo.getDataVencimento());
    }

    @Test
    void shouldNotUpdateWhenEntityIsNull() {
        TituloRequest request = new TituloRequest(
                null, "Teste", new BigDecimal("100.00"),
                LocalDate.of(2026, 6, 1), TipoTitulo.PAGAR, null, null, List.of()
        );
        assertDoesNotThrow(() -> mapper.updateEntity(null, request, List.of()));
    }

    @Test
    void shouldNotUpdateWhenRequestIsNull() {
        assertDoesNotThrow(() -> mapper.updateEntity(titulo, null, List.of()));
    }

    @Test
    void shouldCountFaturamentosInDto() {
        titulo.getFaturamentos(); // list is empty by builder default
        TituloResponse dto = mapper.toDto(titulo);
        assertEquals(0, dto.quantidadeFaturamentos());
    }

    @Test
    void shouldHandleNullCentrosDeCustoInDto() {
        titulo.setCentroDeCusto(null);
        TituloResponse dto = mapper.toDto(titulo);
        assertNotNull(dto);
        assertTrue(dto.centrosDeCusto().isEmpty());
    }
}