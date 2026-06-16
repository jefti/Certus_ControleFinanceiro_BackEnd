package com.projeto.financeiro.dto.mapper;

import com.projeto.financeiro.dto.request.CentroDeCustoRequest;
import com.projeto.financeiro.dto.response.CentroDeCustoResponse;
import com.projeto.financeiro.entity.CentroDeCusto;
import com.projeto.financeiro.entity.Usuario;
import com.projeto.financeiro.security.TextSanitizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CentroDeCustoMapperTest {

    private final CentroDeCustoMapper mapper = new CentroDeCustoMapper(new TextSanitizer());

    private Usuario usuario;
    private CentroDeCusto entity;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Marcelo");

        entity = new CentroDeCusto();
        entity.setId(1L);
        entity.setDescricao("Alimentação");
        entity.setObservacao("Gastos mensais");
        entity.setUsuario(usuario);
    }

    @Test
    void shouldMapEntityToDto() {
        CentroDeCustoResponse dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Alimentação", dto.descricao());
        assertEquals("Gastos mensais", dto.observacao());
    }

    @Test
    void shouldReturnNullToDtoWhenEntityIsNull() {
        assertNull(mapper.toDto(null));
    }

    @Test
    void shouldMapRequestToEntity() {
        CentroDeCustoRequest request = new CentroDeCustoRequest(null, "Transporte", "Combustível");

        CentroDeCusto result = mapper.toEntity(request, usuario);

        assertNotNull(result);
        assertEquals("Transporte", result.getDescricao());
        assertEquals("Combustível", result.getObservacao());
        assertEquals(usuario, result.getUsuario());
    }

    @Test
    void shouldReturnNullToEntityWhenRequestIsNull() {
        assertNull(mapper.toEntity(null, usuario));
    }

    @Test
    void shouldUpdateEntityFromRequest() {
        CentroDeCustoRequest request = new CentroDeCustoRequest(1L, "Saúde", "Plano de saúde");

        mapper.updateEntity(entity, request);

        assertEquals("Saúde", entity.getDescricao());
        assertEquals("Plano de saúde", entity.getObservacao());
    }

    @Test
    void shouldNotUpdateWhenEntityIsNull() {
        assertDoesNotThrow(() -> mapper.updateEntity(null, new CentroDeCustoRequest(null, "Saúde", null)));
    }

    @Test
    void shouldNotUpdateWhenRequestIsNull() {
        assertDoesNotThrow(() -> mapper.updateEntity(entity, null));
    }
}
